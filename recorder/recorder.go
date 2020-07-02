package recorder

import (
	"RM-RecordServer/config"
	"RM-RecordServer/docker"
	"RM-RecordServer/logger"
	"errors"
	"sync"
	"time"
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
	ErrSessionIDAlreadyExists = errors.New("Session ID Already Exists")
	ErrNotFoundSessionID      = errors.New("Not Found Session ID")
	ErrInternalError          = errors.New("Internal Error")
)

var timeoutCh chan string

func init() {
	timeoutCh = make(chan string, 512)
	go timeoutHandler()
}

func NewRecording(param RecordingParam) error {
	_, ok := recorderMap[param.SessionID]
	if ok {
		return ErrSessionIDAlreadyExists
	}

	containerParam := docker.ContainerParam{
		VideoID:     param.SessionID,
		VideoName:   param.SessionID,
		Resolution:  param.Resolution,
		Framerate:   param.Framerate,
		VideoFormat: config.DefaultVideoFormat,
		LayoutURL:   config.LayoutURL,
	}

	constainerID, err := docker.RunContainer(containerParam)
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

func FindRecording(sessionID string) (string, error) {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	r, ok := recorderMap[sessionID]
	if ok {
		return r.containerID, nil
	}

	return "", ErrNotFoundSessionID
}

func DelRecording(sessionID string, reason string) error {
	logger.Infof("recording stop. (id:%s reason:%s)", sessionID, reason)

	recorderMapMux.Lock()
	defer recorderMapMux.Unlock()

	r, ok := recorderMap[sessionID]
	if !ok {
		return ErrNotFoundSessionID
	}

	r.timeout.Stop()

	docker.StopContainer(r.containerID)

	delete(recorderMap, sessionID)
	return nil
}

func ListSessionIDs() []string {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	var sessionIds []string
	for _, r := range recorderMap {
		sessionIds = append(sessionIds, r.sessionID)
	}

	return sessionIds
}

func ExistSessionID(sessionID string) bool {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	_, ok := recorderMap[sessionID]
	return ok
}

func timeoutHandler() {
	defer func() {
		logger.Info("stop timeoutHandler")
	}()

	for {
		select {
		case sessionID := <-timeoutCh:
			DelRecording(sessionID, "timeout")
		}
	}
}
