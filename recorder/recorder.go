package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/database"
	"RM-RecordServer/dockerclient"
	"RM-RecordServer/logger"
	"RM-RecordServer/util"
	"context"
	"strings"
	"sync"
	"time"

	"github.com/sirupsen/logrus"
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

	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	downloadDockerImage(ctx)
	restoreRecordingFromContainer(ctx)
}

func makeRecordingID(sessionID string) string {
	return sessionID + "-" + util.RandomString(8)
}

func getSessionID(recordingID string) string {
	return strings.Split(recordingID, "-")[0]
}

func NewRecording(ctx context.Context, param RecordingParam) (string, error) {
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

	containerID, err := dockerclient.RunContainer(ctx, containerParam)
	if err != nil {
		return recordingId, ErrInternalError
	}

	timeout := time.Duration(param.TimeLimit) * time.Second
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

func StopRecording(ctx context.Context, recordingID string, reason string) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Infof("recording stop. (id:%s reason:%s)", recordingID, reason)

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	r, ok := recorderMap[recordingID]
	if !ok {
		return ErrNotFoundRecordingID
	}

	r.stop(ctx)
	return nil
}

func StopRecordingBySessionID(ctx context.Context, sessionID string) ([]string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	var recordingIDs []string

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()
	for id, recording := range recorderMap {
		if recording.sessionID == sessionID {
			recordingIDs = append(recordingIDs, id)

			recording.stop(ctx)
		}
	}
	log.Debugf("stop recording: sessionID:%s recordingIDs:%+v", sessionID, recordingIDs)

	return recordingIDs, nil
}

func RestoreRecording(ctx context.Context, recordingID string, containerID string, recordingTimeLimit int64) {
	logger.Infof("RestoreRecording: recordingId:%s containerID:%s recordingTimeLimit:%d)", recordingID, containerID, recordingTimeLimit)

	if recordingTimeLimit <= 0 {
		dockerclient.StopContainer(ctx, containerID)
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
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	defer func() {
		logger.Info("stop timeoutHandler")
	}()

	for {
		select {
		case recordingID := <-timeoutCh:
			StopRecording(ctx, recordingID, "timeout")
		}
	}
}

func GetNumCurrentRecordings() int {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	return len(recorderMap)
}

func garbageCollector() {
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	period := viper.GetInt("record.recordingFilePeriod")
	if period == 0 {
		log.Info("disable recording file garbageCollector")
		return
	}
	period = period * 24 * 60 * 60

	gcPeriod := viper.GetInt("general.garbageCollectPeriod")
	ticker := time.NewTicker(time.Duration(gcPeriod) * time.Hour)
	defer ticker.Stop()

	for range ticker.C {
		now := time.Now().UTC().Unix()
		list, _, err := ListRecordingFiles(ctx, nil, true)
		if err != nil {
			log.Error(err)
			continue
		}

		for _, info := range list {
			remainDays := int64(period) - (now - time.Time(info.CreateAt).Unix())
			logger.Debug("recordingId:", info.RecordingID, " file:", info.Filename, " create:", info.CreateAt, " remain:", remainDays)
			if remainDays < 0 {
				// delete file record from DB.
				filter := &data.Filter{RecordingID: &info.RecordingID}
				deleteOnDB(ctx, filter)

				// delete file on local storage.
				removeFile(ctx, info.FullPath)
			}
		}
	}
}

func downloadDockerImage(ctx context.Context) {
	err := dockerclient.DownloadDockerImage(ctx)
	if err != nil {
		panic(err)
	}
}

func restoreRecordingFromContainer(ctx context.Context) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Info("Start: Restore Recording From Container")
	constainers := dockerclient.ListContainers(ctx)
	now := time.Now().UTC().Unix()

	for _, container := range constainers {
		recordingTimeLimit := container.EndTime - now
		RestoreRecording(ctx, container.RecordingID, container.ID, recordingTimeLimit)
	}
	log.Info("End: Restore Recording From Container")
}

func (r *recording) stop(ctx context.Context) {
	r.timeout.Stop()
	delete(recorderMap, r.ID)
	go func() {
		dockerclient.StopContainer(ctx, r.containerID)
		insertIntoDB(ctx, r.ID)
	}()
}
