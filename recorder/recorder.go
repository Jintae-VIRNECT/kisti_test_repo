package recorder

import (
	"RM-RecordServer/database"
	"RM-RecordServer/dockerclient"
	"RM-RecordServer/logger"
	"RM-RecordServer/util"
	"strings"
	"sync"
	"time"

	"github.com/spf13/viper"
)

type recording struct {
	ID          string
	sessionID   string
	containerID string
	timeout     *time.Timer
}

type RecordingParam struct {
	SessionID  string
	Token      string
	Resolution string
	Framerate  uint
	TimeLimit  int
	Filename   string
	MetaData   interface{}
}

type RecordingFileInfo struct {
	RecordingID string      `json:"recordingId"`
	SessionID   string      `json:"sessionId"`
	Filename    string      `json:"filename"`
	FullPath    string      `json:"fullPath"`
	Duration    int         `json:"duration"`
	Size        int         `json:"size"`
	Resolution  string      `json:"resolution"`
	Framerate   uint        `json:"framerate"`
	CreateAt    time.Time   `json:"createAt"`
	MetaData    interface{} `json:"metaData,omitempty"`
}

var recorderMap = map[string]*recording{}
var recorderMapMux sync.RWMutex

var timeoutCh chan string

func Init() {
	driver := viper.GetString("database.driver")
	param := viper.GetString("database.param")
	if len(driver) > 0 && len(param) > 0 {
		db = database.NewTable(driver, param)
	}

	timeoutCh = make(chan string, 512)
	go timeoutHandler()
	go garbageCollector()
}

func makeRecordingID(sessionID string) string {
	return sessionID + "-" + util.RandomString(8)
}

func getSessionID(recordingID string) string {
	return strings.Split(recordingID, "-")[0]
}

func NewRecording(param RecordingParam) (string, error) {
	var recordingId = makeRecordingID(param.SessionID)

	if len(param.Filename) == 0 {
		param.Filename = param.SessionID
	}

	containerParam := dockerclient.ContainerParam{
		RecordingID: recordingId,
		Token:       param.Token,
		VideoID:     recordingId,
		VideoName:   param.Filename,
		Resolution:  param.Resolution,
		Framerate:   param.Framerate,
		VideoFormat: viper.GetString("record.defaultVideoFormat"),
		LayoutURL:   viper.GetString("record.layoutURL"),
		TimeLimit:   param.TimeLimit,
		SessionID:   param.SessionID,
		MetaData:    param.MetaData,
	}

	containerID, err := dockerclient.RunContainer(containerParam)
	if err != nil {
		return recordingId, ErrInternalError
	}

	timeout := time.Duration(param.TimeLimit) * time.Minute
	timer := time.AfterFunc(timeout, func() {
		timeoutCh <- recordingId
	})

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	r := &recording{recordingId, param.SessionID, containerID, timer}
	recorderMap[recordingId] = r
	return recordingId, nil
}

func FindRecording(recordingID string) (string, error) {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	r, ok := recorderMap[recordingID]
	if ok {
		return r.containerID, nil
	}

	return "", ErrNotFoundRecordingID
}

func StopRecording(recordingID string, reason string) error {
	logger.Infof("recording stop. (id:%s reason:%s)", recordingID, reason)

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	r, ok := recorderMap[recordingID]
	if !ok {
		return ErrNotFoundRecordingID
	}

	r.timeout.Stop()

	dockerclient.StopContainer(r.containerID)

	insertIntoDB(recordingID)

	delete(recorderMap, recordingID)
	return nil
}

func RestoreRecording(recordingID string, containerID string, recordingTimeLimit int64) {
	logger.Infof("RestoreRecording: recordingId:%s containerID:%s recordingTimeLimit:%d)", recordingID, containerID, recordingTimeLimit)

	if recordingTimeLimit <= 0 {
		dockerclient.StopContainer(containerID)
		return
	}

	timeout := time.Duration(recordingTimeLimit) * time.Second
	timer := time.AfterFunc(timeout, func() {
		timeoutCh <- recordingID
	})

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	sessionID := getSessionID(recordingID)
	r := &recording{recordingID, sessionID, containerID, timer}
	recorderMap[recordingID] = r
}

func ListRecordingIDs() []string {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	var recordingIds []string
	for _, r := range recorderMap {
		recordingIds = append(recordingIds, r.ID)
	}

	return recordingIds
}

func ExistRecordingID(recordingID string) bool {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	_, ok := recorderMap[recordingID]
	return ok
}

func timeoutHandler() {
	defer func() {
		logger.Info("stop timeoutHandler")
	}()

	for {
		select {
		case recordingID := <-timeoutCh:
			StopRecording(recordingID, "timeout")
		}
	}
}

func GetNumCurrentRecordings() int {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	return len(recorderMap)
}

func garbageCollector() {
	period := viper.GetInt("record.recordingFilePeriod")
	if period == 0 {
		logger.Info("disable recording file garbageCollector")
		return
	}
	period = period * 24 * 60 * 60

	gcPeriod := viper.GetInt("general.garbageCollectPeriod")
	ticker := time.NewTicker(time.Duration(gcPeriod) * time.Hour)
	defer ticker.Stop()

	for range ticker.C {
		now := time.Now().UTC().Unix()
		list, _, err := ListRecordingFiles(nil, true)
		if err != nil {
			logger.Error(err)
			continue
		}

		for _, info := range list {
			remainDays := int64(period) - (now - time.Time(info.CreateAt).Unix())
			logger.Debug("recordingId:", info.RecordingID, " file:", info.Filename, " create:", info.CreateAt, " remain:", remainDays)
			if remainDays < 0 {
				removeFile(info.FullPath)
			}
		}
	}
}
