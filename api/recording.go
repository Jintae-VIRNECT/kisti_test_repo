package api

import (
	"RM-RecordServer/data"
	"RM-RecordServer/recorder"
	"RM-RecordServer/util"
	"encoding/json"
	"fmt"
	"path/filepath"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

type StartRecordingRequest struct {
	// session id of room
	SessionID string `json:"sessionId" binding:"required" example:"session_1"`
	// token
	Token string `json:"token" binding:"required" example:"wss://192.168.0.9:8000?sessionId=ses_PAtRKcOQSX&token=tok_G5Pgb8cIRTfq8U3E&role=PUBLISHER&version=2.0.1"`
	// video resolution
	Resolution string `json:"resolution,omitempty" binding:"oneof=480p 720p 1080p" enums:"480p, 720p, 1080p" default:"720p" example:"720p"`
	// video framerate
	Framerate int `json:"framerate,omitempty" binding:"min=1,max=30" mininum:"1" maxinum:"30" default:"20" example:"20"`
	// recording time (in minutes)
	RecordingTimeLimit int `json:"recordingTimeLimit,omitempty" binding:"min=5,max=60" mininum:"5" maxinum:"60" default:"5" example:"5"`
	// recording filename. the supported file extensions are 'mp4', 'wmv'.
	RecordingFilename string `json:"recordingFilename,omitempty" example:"2020-08-05_10:00:00.mp4"`
	// meta data in json format
	MetaData interface{} `json:"metaData,omitempty"`
}

type RecordingInfo struct {
	RecordingID string `json:"recordingId"`
	Duration    int    `json:"duration"`  // the duration of the recording in seconds
	TimeLimit   int    `json:"timeLimit"` // time limitation of this recording (in minutes)
	Status      string `json:"status"`    // status of recording <br> - preparing: not yet started <br> - recording: in progress
}

type StartRecordingResponse struct {
	RecordingID string `json:"recordingId"`
}

type StopRecordingResponse struct {
	RecordingIDs []string `json:"recordingIds"`
}

type ListRecordingResponse struct {
	Recordings []RecordingInfo `json:"infos"`
}

type RecordingQuery struct {
	// search by session id
	SessionID string `json:"sessionId" example:"sessionId"`
}

// @Summary Start Recording
// @Description Start Recording
// @tags Recording
// @Accept json
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param body body StartRecordingRequest true "information for recording"
// @Success 200 {object} successResponse{data=StartRecordingResponse}
// @Failure 1001 {} json "{"code":1001,"message":"Too Many Recordings","service":"remote-record-server","data":{}}"
// @Failure 1002 {} json "{"code":1002,"message":"Not Enough Free Space","service":"remote-record-server","data":{}}"
// @Failure 8001 {} json "{"code":8001,"message":"error message","service":"remote-record-server","data":{}}"
// @Failure 9999 {} json "{"code":9999,"message":"error message","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings [post]
func StartRecording(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)

	workspaceID := c.Param("workspaceId")
	userID := c.Param("userId")

	req := StartRecordingRequest{
		Resolution:         viper.GetString("record.defaultResolution"),
		Framerate:          viper.GetInt("record.defaultFramerate"),
		RecordingTimeLimit: viper.GetInt("record.defaultRecordingTimeLimit"),
		RecordingFilename:  "",
	}
	err := c.ShouldBindJSON(&req)
	if err != nil {
		log.Error("bind json fail:", err)
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}

	log.Debugf("StartRecording:%+v", req)

	_, err = json.Marshal(req.MetaData)
	if err != nil {
		log.Error("metaData parsing fail:", err)
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}

	max := viper.GetInt("record.numOfConcurrentRecordings")
	cur := recorder.GetNumCurrentRecordings()
	if max <= cur {
		log.Errorf("too many recording: current: %d limit:%d", cur, max)
		sendResponseWithError(c, NewErrorTooManyRecordings())
		return
	}

	diskFreeThreshold := viper.GetFloat64("record.diskFreeThreshold")
	if diskFreeThreshold > 0 {
		diskUsage, _ := util.DiskUsage(viper.GetString("record.dir"))
		if float64(diskUsage.Free)/float64(util.GB) < viper.GetFloat64("record.diskFreeThreshold") {
			log.Errorf("not enough free space: all:%f used:%f free:%f",
				float64(diskUsage.All)/float64(util.GB),
				float64(diskUsage.Used)/float64(util.GB),
				float64(diskUsage.Free)/float64(util.GB))
			sendResponseWithError(c, NewErrorInsufficientStorage())
			return
		}
	}

	diskUsageLimit := viper.GetFloat64("record.diskUsageLimit")
	if diskUsageLimit > 0 {
		usageSum := 0
		infos, _ := recorder.ListRecordingFilesOnLocalStorage(c.Request.Context())
		for _, info := range infos {
			usageSum += info.Size
		}
		log.Debug("recording file usage size:", usageSum)
		if diskUsageLimit*util.GB < float64(usageSum) {
			sendResponseWithError(c, NewErrorInsufficientStorage())
			return
		}
	}

	resolution, err := convertResolution(req.Resolution)
	log.Debug("resolution:", resolution)
	if err != nil {
		log.Error(err)
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}

	err = checkFileFormat(req.RecordingFilename)
	if err != nil {
		log.Error(err)
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}

	// The actual recording time is shorter than the requested limit. So, give me about 10 seconds.
	timeLimit := req.RecordingTimeLimit * 60

	param := recorder.RecordingParam{
		SessionID:   data.SessionID(req.SessionID),
		WorkspaceID: data.WorkspaceID(workspaceID),
		UserID:      userID,
		Token:       req.Token,
		Resolution:  resolution,
		Framerate:   req.Framerate,
		TimeLimit:   timeLimit,
		Filename:    req.RecordingFilename,
		MetaData:    req.MetaData,
	}

	recordingID, err := recorder.NewRecording(c.Request.Context(), param)
	if err != nil {
		log.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}

	sendResponseWithSuccess(c, StartRecordingResponse{recordingID.String()})
}

func convertResolution(resolution string) (string, error) {
	switch resolution {
	case "480p":
		return "640x480", nil
	case "720p":
		return "1280x720", nil
	case "1080p":
		return "1920x1080", nil
	default:
		return "", fmt.Errorf("not supported resolution: %s", resolution)
	}
}

func checkFileFormat(filename string) error {
	ext := strings.TrimLeft(filepath.Ext(filename), ".")
	if len(ext) == 0 {
		return nil
	}
	if ok := viper.IsSet(strings.Join([]string{"format", ext}, ".")); ok == false {
		return fmt.Errorf("not supported format: %s", ext)
	}

	return nil
}

// @Summary Stop Recording
// @Description Stop Recording
// @tags Recording
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param id path string true "recording id"
// @Success 200 {object} successResponse{data=StopRecordingResponse}
// @Failure 1000 {} json "{"code":1000,"message":"Not Found Recording ID","service":"remote-record-server","data":{}}"
// @Failure 1003 {} json "{"code":1003,"message":"Recording Has Not Started","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings/{id} [delete]
func StopRecording(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)

	recordingID := data.RecordingID(c.Param("id"))
	workspaceID := data.WorkspaceID(c.Param("workspaceId"))

	log.Debug("stop recording (id:", recordingID, ")")

	exist := recorder.ExistRecordingID(recordingID, workspaceID)
	if exist == false {
		log.Error("stop recording: ", recorder.ErrNotFoundRecordingID.Error())
		sendResponseWithError(c, NewErrorNotFoundRecordingID())
		return
	}

	err := recorder.StopRecording(c.Request.Context(), recordingID, workspaceID, recorder.Stopped)
	if err == recorder.ErrRecordingHasNotStarted {
		log.WithError(err).Error(err)
		sendResponseWithError(c, NewRecordingHasNotStarted())
		return
	} else if err != nil {
		log.WithError(err).Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}

	sendResponseWithSuccess(c, StopRecordingResponse{[]string{recordingID.String()}})
}

// @Summary Stop Recordings By SessionId
// @Description Stop Recordings By SessionId
// @tags Recording
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param sessionId query RecordingQuery false "description"
// @Success 200 {object} successResponse{data=StopRecordingResponse}
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings [delete]
func StopRecordingBySessionID(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)

	sessID, _ := c.GetQuery("sessionId")
	sessionID := data.SessionID(sessID)
	workspaceID := data.WorkspaceID(c.Param("workspaceId"))

	log.Debugf("stop recordings session_id:%s workspaceID:%s", sessionID, workspaceID)

	body := StopRecordingResponse{make([]string, 0)}
	ids, _ := recorder.StopRecordingBySessionID(c.Request.Context(), workspaceID, sessionID)
	body.RecordingIDs = append(body.RecordingIDs, ids...)
	sendResponseWithSuccess(c, body)
}

// @Summary List Recordings
// @Description List Recordings
// @tags Recording
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param sessionId query RecordingQuery false "description"
// @Success 200 {object} successResponse{data=ListRecordingResponse}
// @Failure 9999 {} json "{"code":9999,"message":"error message","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/recordings [get]
func ListRecordings(c *gin.Context) {
	workspaceID := data.WorkspaceID(c.Param("workspaceId"))

	var sessionID data.SessionID = ""
	if sessID, ok := c.GetQuery("sessionId"); ok == true {
		sessionID = data.SessionID(sessID)
	}

	body := ListRecordingResponse{make([]RecordingInfo, 0)}
	for _, r := range recorder.ListRecordingIDs(c.Request.Context(), workspaceID, sessionID) {
		body.Recordings = append(body.Recordings, RecordingInfo{r.RecordingID, r.Duration, r.TimeLimit / 60, r.Status})
	}

	sendResponseWithSuccess(c, body)
}
