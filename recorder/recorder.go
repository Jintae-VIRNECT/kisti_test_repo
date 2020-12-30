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

type RecordingInfo struct {
	RecordingID string
	Duration    int    // in seconds
	TimeLimit   int    // in seconds
	Status      string // preparing, recording
}

type Reason int

const (
	Timeout Reason = iota
	Stopped
)

type Status string

const (
	Preparing = "preparing"
	Recording = "recording"
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

func newRecorder() *recorder {
	return &recorder{
		recordingMap: map[data.RecordingID]*recording{},
		timeoutCh:    make(chan recordingTimeoutEvent, 512),
	}
}

func (r *recorder) timeoutHandler() {
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	defer logger.Info("stop timeoutHandler")

	for {
		select {
		case evt := <-r.timeoutCh:
			StopRecording(ctx, evt.recordingID, evt.workspaceID, Timeout)
		}
	}
}

func (r *recorder) prepareRecording(recordingID data.RecordingID, sessionID data.SessionID, workspaceID data.WorkspaceID,
	userID string, timeLimit int) (*recording, error) {
	r.mapMux.Lock()
	defer r.mapMux.Unlock()

	_, ok := r.recordingMap[recordingID]
	if ok == true {
		return nil, ErrRecordingIDAlreadyExists
	}

	rec := &recording{
		id:          recordingID,
		sessionID:   sessionID,
		workspaceID: workspaceID,
		creator:     userID,
		status:      Preparing,
		timeLimit:   timeLimit,
	}
	r.recordingMap[recordingID] = rec

	return rec, nil
}

func (r *recorder) updateRecording(recordingID data.RecordingID, sessionID data.SessionID, workspaceID data.WorkspaceID,
	userID string, containerID string, createTime time.Time, timeLimit int, timer *time.Timer) (*recording, error) {
	r.mapMux.Lock()
	defer r.mapMux.Unlock()

	rec, ok := r.recordingMap[recordingID]
	if ok == false {
		return nil, ErrNotFoundRecordingID
	}

	rec.id = recordingID
	rec.sessionID = sessionID
	rec.workspaceID = workspaceID
	rec.creator = userID
	rec.containerID = containerID
	rec.createTime = createTime
	rec.timeLimit = timeLimit
	rec.timeout = timer
	rec.status = Recording

	return rec, nil
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
	createTime  time.Time
	timeLimit   int
	timeout     *time.Timer
	status      Status
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

var mainRecorder = newRecorder()

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
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	recordingID := data.RecordingID(uuid.New().String())

	if len(param.Filename) == 0 {
		param.Filename = param.SessionID.String()
	}

	rec, err := mainRecorder.prepareRecording(recordingID, param.SessionID, param.WorkspaceID, param.UserID, param.TimeLimit)
	if err != nil {
		return "", err
	}
	defer func() {
		if rec.status == Preparing {
			mainRecorder.removeRecording(recordingID, param.WorkspaceID)
		}
	}()

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
		log.WithError(err).Error("delete files due to failed recording request")
		removeLocalFile(ctx, getInfoFilename(recordingID))
		return recordingID, ErrInternalError
	}

	timeout := time.Duration(param.TimeLimit) * time.Second
	timer := time.AfterFunc(timeout, func() {
		mainRecorder.timeoutCh <- recordingTimeoutEvent{recordingID, param.WorkspaceID}
	})

	now := time.Now()
	mainRecorder.updateRecording(recordingID, param.SessionID, param.WorkspaceID, param.UserID, containerID, now, param.TimeLimit, timer)

	writeCreateTime(ctx, recordingID, now)
	return recordingID, nil
}

func StopRecording(ctx context.Context, recordingID data.RecordingID, workspaceID data.WorkspaceID, reason Reason) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Infof("recording stop. (id:%s reason:%s)", recordingID, reason.String())

	recordings := mainRecorder.findRecordings(&recordingID, &workspaceID, nil)
	if len(recordings) == 0 {
		return ErrNotFoundRecordingID
	}

	rec := recordings[0]
	if rec.status == Preparing {
		return ErrRecordingHasNotStarted
	}
	mainRecorder.removeRecording(recordingID, workspaceID)
	rec.stop(ctx, reason)

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
		if r.status == Preparing {
			log.Infof("status is preparing. skip: %s", r.id)
			continue
		}
		recordingIDs = append(recordingIDs, r.id.String())
		r.stop(ctx, Stopped)
		mainRecorder.removeRecording(r.id, r.workspaceID)
	}

	log.Debugf("stop recording: sessionID:%s recordingIDs:%+v", sessionID, recordingIDs)

	return recordingIDs, nil
}

func RestoreRecording(ctx context.Context, recordingID data.RecordingID, sessionID data.SessionID, workspaceID data.WorkspaceID,
	userID string, containerID string, createTime time.Time, timeLimit int) {
	now := time.Now().Unix()
	recordingTimeLeft := timeLimit - int(now-createTime.Unix())

	logger.Infof("RestoreRecording: recordingId:%s workspaceId:%s userId:%s containerID:%s recordingTimeLeft:%d)", recordingID, workspaceID, userID, containerID, recordingTimeLeft)

	if recordingTimeLeft <= 0 {
		dockerclient.StopContainer(ctx, containerID)

		info, err := readInfoFile(ctx, recordingID)
		if err != nil {
			logger.Error(err)
			return
		}

		if err := upload(ctx, info); err != nil {
			logger.Error("upload fail. err:", err)
			return
		}

		removeLocalFile(ctx, info.FullPath)
		return
	}

	timeout := time.Duration(recordingTimeLeft) * time.Second
	timer := time.AfterFunc(timeout, func() {
		mainRecorder.timeoutCh <- recordingTimeoutEvent{recordingID, workspaceID}
	})

	mainRecorder.prepareRecording(recordingID, sessionID, workspaceID, userID, timeLimit)
	mainRecorder.updateRecording(recordingID, sessionID, workspaceID, userID, containerID, createTime, timeLimit, timer)
}

func ListRecordingIDs(ctx context.Context, workspaceID data.WorkspaceID, sessionID data.SessionID) []RecordingInfo {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	var recordings []RecordingInfo

	log.Debug("sessionId:", sessionID)
	now := time.Now()
	for _, r := range mainRecorder.findRecordings(nil, &workspaceID, nil) {
		if len(sessionID) > 0 && sessionID != r.sessionID {
			continue
		}
		var duration int
		if r.createTime.IsZero() {
			duration = 0
		} else {
			duration = int(now.Sub(r.createTime).Seconds())
		}
		if duration < 0 {
			duration = 0
		}
		info := RecordingInfo{
			RecordingID: r.id.String(),
			Duration:    duration,
			TimeLimit:   r.timeLimit,
			Status:      string(r.status),
		}
		recordings = append(recordings, info)
	}

	return recordings
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
	for _, container := range constainers {
		recordID := data.RecordingID(container.RecordingID)
		createTime, err := readCreateTime(ctx, recordID)
		if err != nil {
			log.Warn("get create time error. endTime:", container.EndTime, " timeLimit:", container.TimeLimit)
			createTime = time.Unix(container.EndTime-int64(container.TimeLimit), 0)
			createTime = createTime.Add(time.Second * 10) // host와 container의 시간이 다르기 때문에 약간의 보정이 필요하다.
			writeCreateTime(ctx, recordID, createTime)
		}
		log.Debug("create time:", createTime.UTC())
		RestoreRecording(ctx,
			recordID,
			data.SessionID(container.SessionID),
			data.WorkspaceID(container.WorkspaceID),
			container.UserID,
			container.ID,
			createTime,
			container.TimeLimit,
		)
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

			createTime, err := readCreateTime(ctx, info.RecordingID)
			if err != nil {
				logEntry.WithError(err).Error("garbageCollector: get create time")
				continue
			}
			marginTime := 60
			if time.Now().Unix() < createTime.Unix()+int64(info.TimeLimit+marginTime) {
				continue
			}

			if err := upload(ctx, info); err != nil {
				logEntry.Error("upload fail. err:", err)
				continue
			}

			removeLocalFile(ctx, info.FullPath)
		}
	}
}
