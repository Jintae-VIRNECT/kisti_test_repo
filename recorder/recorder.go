package recorder

import (
	"RM-RecordServer/dockerclient"
	"RM-RecordServer/logger"
	"RM-RecordServer/util"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"sync"
	"syscall"
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
	Resolution string
	Framerate  uint
	TimeLimit  int
	Filename   string
}

type RecordingFileInfo struct {
	Filename   string `json:"filename"`
	FullPath   string `json:"fullPath"`
	Duration   int    `json:"duration"`
	Size       int    `json:"size"`
	CreateTime string `json:"ceateTime"`
}

var recorderMap = map[string]*recording{}
var recorderMapMux sync.RWMutex

var (
	ErrRecordingIDAlreadyExists = errors.New("Recording ID Already Exists")
	ErrNotFoundRecordingID      = errors.New("Not Found Recording ID")
	ErrInternalError            = errors.New("Internal Error")
)

var timeoutCh chan string

func init() {
	timeoutCh = make(chan string, 512)
	go timeoutHandler()
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
		VideoID:     recordingId,
		VideoName:   param.Filename,
		Resolution:  param.Resolution,
		Framerate:   param.Framerate,
		VideoFormat: viper.GetString("record.defaultVideoFormat"),
		LayoutURL:   viper.GetString("record.layoutURL"),
		TimeLimit:   param.TimeLimit,
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

func DelRecording(recordingID string, reason string) error {
	logger.Infof("recording stop. (id:%s reason:%s)", recordingID, reason)

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	r, ok := recorderMap[recordingID]
	if !ok {
		return ErrNotFoundRecordingID
	}

	r.timeout.Stop()

	dockerclient.StopContainer(r.containerID)

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
			DelRecording(recordingID, "timeout")
		}
	}
}

func GetNumCurrentRecordings() int {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	return len(recorderMap)
}

func ListRecordingFiles() ([]RecordingFileInfo, error) {
	var files []string
	infos := []RecordingFileInfo{}
	root := viper.GetString("record.dir")
	if _, err := os.Stat(root); os.IsNotExist(err) {
		return infos, err
	}
	err := filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
		if info.IsDir() {
			return nil
		}
		ext := ".info"
		if filepath.Ext(path) == ext {
			files = append(files, path)
		}
		return nil
	})
	if err != nil {
		return infos, err
	}
	for _, file := range files {
		info, err := readInfoFile(file)
		if err != nil {
			continue
		}
		infos = append(infos, info)
	}
	return infos, nil
}

func RemoveRecordingFiles() (int, error) {
	count, err := util.RemoveContents(viper.GetString("record.dir"))
	logger.Info("delete all recording files. count:", count)
	return count, err
}

func readInfoFile(file string) (RecordingFileInfo, error) {
	info := RecordingFileInfo{}

	infoFile, err := os.Open(file)
	if err != nil {
		logger.Error("open file:", err)
		return info, err
	}
	defer infoFile.Close()
	byteValue, err := ioutil.ReadAll(infoFile)
	if err != nil {
		logger.Error("json parse file:", err)
		return info, err
	}
	var result map[string]interface{}
	json.Unmarshal([]byte(byteValue), &result)

	format := result["format"].(map[string]interface{})
	duration, _ := strconv.ParseFloat(format["duration"].(string), 32)
	filenameWithPath := format["filename"].(string)
	fullPath := viper.GetString("record.dir") + "/" + strings.TrimPrefix(filenameWithPath, viper.GetString("record.dirOnDocker"))
	finfo, err := os.Stat(fullPath)
	if err != nil {
		logger.Error(err)
		return info, err
	}
	stat := finfo.Sys().(*syscall.Stat_t)
	ts := stat.Ctim

	info.Filename = filepath.Base(filenameWithPath)
	info.FullPath = viper.GetString("record.dirOnHost") + "/" + strings.TrimPrefix(filenameWithPath, viper.GetString("record.dirOnDocker"))
	info.Duration = int(duration)
	info.Size, _ = strconv.Atoi(format["size"].(string))
	info.CreateTime = fmt.Sprintln(time.Unix(int64(ts.Sec), int64(ts.Nsec)).UTC())

	return info, nil
}
