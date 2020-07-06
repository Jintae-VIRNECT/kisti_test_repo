package recorder

import (
	"RM-RecordServer/dockerclient"
	"RM-RecordServer/logger"
	"errors"
	"sync"
	"time"

	"github.com/spf13/viper"
)

type recording struct {
	sessionID   string
	containerID string
	timeout     *time.Timer
}

type RecordingParam struct {
	SessionID          string
	Resolution         string
	Framerate          int
	RecordingTimeLimit int
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

func NewRecording(param RecordingParam) error {
	_, ok := recorderMap[param.SessionID]
	if ok {
		return ErrRecordingIDAlreadyExists
	}

	containerParam := dockerclient.ContainerParam{
		VideoID:     param.SessionID,
		VideoName:   param.SessionID,
		Resolution:  param.Resolution,
		Framerate:   param.Framerate,
		VideoFormat: viper.GetString("record.defaultVideoFormat"),
		LayoutURL:   viper.GetString("record.layoutURL"),
	}

	constainerID, err := dockerclient.RunContainer(containerParam)
	if err != nil {
		return ErrInternalError
	}

	timeout := time.Duration(param.RecordingTimeLimit) * time.Minute
	timer := time.AfterFunc(timeout, func() {
		timeoutCh <- param.SessionID
	})

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	r := &recording{param.SessionID, constainerID, timer}
	recorderMap[param.SessionID] = r
	return nil
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

func ListRecordingIDs() []string {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	var recordingIds []string
	for _, r := range recorderMap {
		recordingIds = append(recordingIds, r.sessionID)
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
