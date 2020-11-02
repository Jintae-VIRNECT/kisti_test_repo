package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/database"
	"RM-RecordServer/dockerclient"
	"RM-RecordServer/logger"
	"context"
	"sync"
	"time"

	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

type RecordingParam struct {
	SessionID   data.SessionID
	WorkspaceID data.WorkspaceID
	UserID      string
	Token       string
	Resolution  string
	Framerate   int
	TimeLimit   int
	Filename    string
	MetaData    interface{}
}

type Reason int

const (
	Timeout Reason = iota
	Stopped
)

func (r Reason) String() string {
	reason := [...]string{"timeout", "stopped"}
	if len(reason) < int(r) {
		return ""
	}

	return reason[r]
}

type recordingTimeoutEvent struct {
	recordingID data.RecordingID
	workspaceID data.WorkspaceID
}
type recorder struct {
	recordingMap map[data.RecordingID]*recording
	mapMux       sync.RWMutex
	timeoutCh    chan recordingTimeoutEvent
}

func (r *recorder) timeoutHandler() {
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	defer func() {
		logger.Info("stop timeoutHandler")
	}()

	for {
		select {
		case evt := <-r.timeoutCh:
			StopRecording(ctx, evt.recordingID, evt.workspaceID, Timeout)
		}
	}
}

func (r *recorder) addRecording(recordingID data.RecordingID, sessionID data.SessionID, workspaceID data.WorkspaceID, userID string, containerID string, timer *time.Timer) *recording {
	r.mapMux.Lock()
	defer r.mapMux.Unlock()

	rec := &recording{
		recordingID,
		sessionID,
		workspaceID,
		userID,
		containerID,
		timer,
	}
	r.recordingMap[recordingID] = rec

	return rec
}

func (r *recorder) findRecordings(recordingID *data.RecordingID, workspaceID *data.WorkspaceID, sessionID *data.SessionID) []*recording {
	r.mapMux.RLock()
	defer r.mapMux.RUnlock()

	res := []*recording{}
	for _, rec := range r.recordingMap {
		if workspaceID != nil && rec.workspaceID != *workspaceID {
			continue
		}
		if recordingID != nil && rec.id != *recordingID {
			continue
		}
		if sessionID != nil && rec.sessionID != *sessionID {
			continue
		}
		res = append(res, rec)
	}

	return res
}

func (r *recorder) removeRecording(recordingID data.RecordingID, workspaceID data.WorkspaceID) *recording {
	r.mapMux.Lock()
	defer r.mapMux.Unlock()

	for id, rec := range r.recordingMap {
		if rec.workspaceID != workspaceID {
			continue
		}

		if id == recordingID {
			delete(r.recordingMap, rec.id)
			return rec
		}
	}

	return nil
}

func (r *recorder) dumpMap(ctx context.Context) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	r.mapMux.RLock()
	defer r.mapMux.RUnlock()

	log.Debug("begin dump recorder map")
	for _, rec := range r.recordingMap {
		log.Debug(rec)
	}
	log.Debug("end dump recorder map")
}

func (r *recorder) size() int {
	r.mapMux.RLock()
	defer r.mapMux.RUnlock()
	return len(r.recordingMap)
}

type recording struct {
	id          data.RecordingID
	sessionID   data.SessionID
	workspaceID data.WorkspaceID
	creator     string
	containerID string
	timeout     *time.Timer
}

func (r *recording) stop(ctx context.Context, reason Reason) {
	r.timeout.Stop()

	// local storage에는 녹화 파일이 성공적으로 생성되었으니
	//   1. stop recording에 대한 응답.
	//   2. storage로 upload 진행.
	//   3. 만약 upload에 실패하면, 추후에 다시 upload 진행
	// 참고: curl로 1G upload 하는데 21s 소요됨.
	go func() {
		logEntry := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
		newCtx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)
		dockerclient.StopContainer(newCtx, r.containerID)

		info, err := readInfoFile(ctx, r.id)
		if err != nil {
			logEntry.Error(err)
			return
		}

		if err := upload(newCtx, info); err != nil {
			logEntry.Error("upload fail. err:", err)
			return
		}

		removeLocalFile(ctx, info.FullPath)
	}()
}

var mainRecorder = recorder{
	recordingMap: map[data.RecordingID]*recording{},
	timeoutCh:    make(chan recordingTimeoutEvent, 512),
}

func Init() {
	driver := viper.GetString("database.driver")
	param := viper.GetString("database.param")
	if len(driver) > 0 && len(param) > 0 {
		db = database.NewTable(driver, param)
	}

	go garbageCollector()
	go mainRecorder.timeoutHandler()

	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	restoreRecordingFromContainer(ctx)
}

func NewRecording(ctx context.Context, param RecordingParam) (data.RecordingID, error) {
	recordingID := data.RecordingID(uuid.New().String())

	if len(param.Filename) == 0 {
		param.Filename = param.SessionID.String()
	}

	containerParam := dockerclient.ContainerParam{
		RecordingID: recordingID,
		Token:       param.Token,
		VideoID:     recordingID.String(),
		Filename:    param.Filename,
		Resolution:  param.Resolution,
		Framerate:   param.Framerate,
		LayoutURL:   viper.GetString("record.layoutURL"),
		TimeLimit:   param.TimeLimit,
		SessionID:   param.SessionID,
		WorkspaceID: param.WorkspaceID,
		UserID:      param.UserID,
		MetaData:    param.MetaData,
	}

	containerID, err := dockerclient.RunContainer(ctx, containerParam)
	if err != nil {
		return recordingID, ErrInternalError
	}

	timeout := time.Duration(param.TimeLimit) * time.Second
	timer := time.AfterFunc(timeout, func() {
		mainRecorder.timeoutCh <- recordingTimeoutEvent{recordingID, param.WorkspaceID}
	})

	mainRecorder.addRecording(recordingID, param.SessionID, param.WorkspaceID, param.UserID, containerID, timer)
	return recordingID, nil
}

func StopRecording(ctx context.Context, recordingID data.RecordingID, workspaceID data.WorkspaceID, reason Reason) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Infof("recording stop. (id:%s reason:%s)", recordingID, reason.String())

	recording := mainRecorder.removeRecording(recordingID, workspaceID)
	if recording == nil {
		return ErrNotFoundRecordingID
	}

	recording.stop(ctx, reason)

	return nil
}

func StopRecordingBySessionID(ctx context.Context, workspaceID data.WorkspaceID, sessionID data.SessionID) ([]string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	var recordingIDs []string
	recordings := mainRecorder.findRecordings(nil, &workspaceID, &sessionID)
	if len(recordings) == 0 {
		return recordingIDs, nil
	}

	for _, r := range recordings {
		recordingIDs = append(recordingIDs, r.id.String())
		r.stop(ctx, Stopped)
		mainRecorder.removeRecording(r.id, r.workspaceID)
	}

	log.Debugf("stop recording: sessionID:%s recordingIDs:%+v", sessionID, recordingIDs)

	return recordingIDs, nil
}

func RestoreRecording(ctx context.Context, recordingID data.RecordingID, sessionID data.SessionID, workspaceID data.WorkspaceID, userID string, containerID string, recordingTimeLimit int64) {
	logger.Infof("RestoreRecording: recordingId:%s workspaceId:%s userId:%s containerID:%s recordingTimeLimit:%d)", recordingID, workspaceID, userID, containerID, recordingTimeLimit)

	if recordingTimeLimit <= 0 {
		dockerclient.StopContainer(ctx, containerID)
		return
	}

	timeout := time.Duration(recordingTimeLimit) * time.Second
	timer := time.AfterFunc(timeout, func() {
		mainRecorder.timeoutCh <- recordingTimeoutEvent{recordingID, workspaceID}
	})

	mainRecorder.addRecording(recordingID, sessionID, workspaceID, userID, containerID, timer)
}

func ListRecordingIDs(workspaceID data.WorkspaceID) []string {
	var recordingIDs []string
	recordings := mainRecorder.findRecordings(nil, &workspaceID, nil)
	for _, r := range recordings {
		recordingIDs = append(recordingIDs, r.id.String())
	}

	return recordingIDs
}

func ExistRecordingID(recordingID data.RecordingID, workspaceID data.WorkspaceID) bool {
	recordings := mainRecorder.findRecordings(&recordingID, &workspaceID, nil)
	if len(recordings) == 0 {
		return false
	}

	return true
}

func GetNumCurrentRecordings() int {
	return mainRecorder.size()
}

func restoreRecordingFromContainer(ctx context.Context) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Info("Start: Restore Recording From Container")
	constainers := dockerclient.ListContainers(ctx)
	now := time.Now().UTC().Unix()

	for _, container := range constainers {
		recordingTimeLimit := container.EndTime - now
		RestoreRecording(ctx,
			data.RecordingID(container.RecordingID),
			data.SessionID(container.SessionID),
			data.WorkspaceID(container.WorkspaceID),
			container.UserID, container.ID, recordingTimeLimit)
	}
	log.Info("End: Restore Recording From Container")
}

func garbageCollector() {
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	period := time.Duration(1) * time.Minute // 1 min
	ticker := time.NewTicker(period)
	defer ticker.Stop()

	for range ticker.C {
		list, err := ListRecordingFilesOnLocalStorage(ctx)
		if err != nil {
			log.Error(err)
			continue
		}

		for _, info := range list {
			logger.Info("upload file. recordingId:", info.RecordingID, " file:", info.Filename, " create:", info.CreateAt)

			if err := upload(ctx, info); err != nil {
				logEntry.Error("upload fail. err:", err)
				continue
			}

			removeLocalFile(ctx, info.FullPath)
		}
	}
}
