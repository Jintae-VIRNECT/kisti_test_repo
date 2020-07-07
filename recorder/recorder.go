package recorder

import (
	"RM-RecordServer/dockerclient"
	"RM-RecordServer/logger"
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
	sessionID   string
	containerID string
	timeout     *time.Timer
}

type RecordingParam struct {
	SessionID          string
	Resolution         string
	Framerate          uint
	RecordingTimeLimit int
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

func GetNumCurrentRecordings() int {
	recorderMapMux.RLock()
	defer recorderMapMux.RUnlock()

	return len(recorderMap)
}

func ListRecordingFiles() ([]RecordingFileInfo, error) {
	var files []string
	infos := []RecordingFileInfo{}
	root := viper.GetString("record.dir")
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
	fullPath := viper.GetString("record.dir") + "/" + strings.TrimPrefix(filenameWithPath, viper.GetString("record.dirInDocker"))
	finfo, _ := os.Stat(fullPath)
	stat := finfo.Sys().(*syscall.Stat_t)
	ts := stat.Ctim

	info.Filename = filepath.Base(filenameWithPath)
	info.FullPath = fullPath
	info.Duration = int(duration)
	info.Size, _ = strconv.Atoi(format["size"].(string))
	info.CreateTime = fmt.Sprintln(time.Unix(int64(ts.Sec), int64(ts.Nsec)).UTC())

	return info, nil
}
