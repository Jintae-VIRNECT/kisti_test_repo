package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"RM-RecordServer/util"
	"encoding/json"
	"fmt"

	"github.com/gin-gonic/gin"
	"github.com/spf13/viper"
)

type StartRecordingRequest struct {
	// session id of room
	SessionID string `json:"sessionId" binding:"required" example:"session_id"`
	// token
	Token string `json:"token" binding:"required" example:"wss://192.168.6.3:8000?sessionId=ses_PAtRKcOQSX&token=tok_G5Pgb8cIRTfq8U3E&role=PUBLISHER&version=2.0.1"`
	// video resolution
	Resolution string `json:"resolution,omitempty" binding:"oneof=480p 720p 1080p" enums:"480p, 720p, 1080p" default:"720p" example:"720p"`
	// video framerate
	Framerate uint `json:"framerate,omitempty" binding:"min=1,max=30" mininum:"1" maxinum:"30" default:"20" example:"20"`
	// recording time
	RecordingTimeLimit int `json:"recordingTimeLimit,omitempty" binding:"min=5,max=60" mininum:"5" maxinum:"60" default:"5" example:"5"`
	// recording filename without extension
	RecordingFilename string `json:"recordingFilename,omitempty" example:"2020-08-05_10:00:00"`
	// user data in json format
	UserData interface{} `json:"userData,omitempty"`
}

type StartRecordingResponse struct {
	RecordingID string `json:"recordingId"`
}

type StopRecordingResponse struct {
	RecordingID string `json:"recordingId"`
	Filename    string `json:"filename"`
	Duration    int    `json:"duration"`
}

type ListRecordingResponse struct {
	RecordingIDs []string `json:"recordingIds"`
}

// @Summary Start Recording
// @Description Start Recording
// @tags Recording
// @Accept json
// @Produce json
// @Param body body StartRecordingRequest true "information for recording"
// @Success 200 {object} response
// @Failure 1001 {} json "{"error":"Too Many Recordings"}""
// @Failure 1002 {} json "{"error":"not enough free space"}"
// @Failure 8001 {} json "{"error":"error message"}"
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/recording [post]
func StartRecording(c *gin.Context) {
	req := StartRecordingRequest{
		Resolution:         viper.GetString("record.defaultResolution"),
		Framerate:          viper.GetUint("record.defaultFramerate"),
		RecordingTimeLimit: viper.GetInt("record.defaultRecordingTimeLimit"),
		RecordingFilename:  "",
	}
	err := c.ShouldBindJSON(&req)
	if err != nil {
		logger.Error("bind json fail:", err)
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}

	logger.Debugf("StartRecording:%+v", req)

	_, err = json.Marshal(req.UserData)
	if err != nil {
		logger.Error("userdata parsing fail:", err)
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}

	max := viper.GetInt("record.numOfConcurrentRecordings")
	cur := recorder.GetNumCurrentRecordings()
	if max <= cur {
		logger.Errorf("too many recording: current: %d limit:%d", cur, max)
		sendResponseWithError(c, NewErrorTooManyRecordings())
		return
	}

	diskFreeThreshold := viper.GetFloat64("record.diskFreeThreshold")
	if diskFreeThreshold > 0 {
		diskUsage, _ := util.DiskUsage(viper.GetString("record.dir"))
		if float64(diskUsage.Free)/float64(util.GB) < viper.GetFloat64("record.diskFreeThreshold") {
			logger.Errorf("not enough free space: all:%f used:%f free:%f",
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
		infos, _ := recorder.ListRecordingFiles()
		for _, info := range infos {
			usageSum += info.Size
		}
		logger.Debug("recording file usage size:", usageSum)
		if diskUsageLimit*util.GB < float64(usageSum) {
			sendResponseWithError(c, NewErrorInsufficientStorage())
			return
		}
	}

	resolution, _ := convertResolution(req.Resolution)
	logger.Debug("resolution:", resolution)

	param := recorder.RecordingParam{
		SessionID:  req.SessionID,
		Token:      req.Token,
		Resolution: resolution,
		Framerate:  req.Framerate,
		TimeLimit:  req.RecordingTimeLimit,
		Filename:   req.RecordingFilename,
		UserData:   req.UserData,
	}

	recordingId, err := recorder.NewRecording(param)
	if err != nil {
		logger.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}

	sendResponseWithSuccess(c, StartRecordingResponse{recordingId})
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

// @Summary Stop Recording
// @Description Stop Recording
// @tags Recording
// @Produce json
// @Param id path string true "recording id"
// @Success 200 {} {object} response
// @Failure 1000 {} json "{ "error": "not found id" }"
// @Router /remote/recorder/recording/{id} [delete]
func StopRecording(c *gin.Context) {
	recordingID := c.Param("id")
	logger.Debug("stop recording (id:", recordingID, ")")

	exist := recorder.ExistRecordingID(recordingID)
	if exist == false {
		logger.Error("stop recording: ", recorder.ErrNotFoundRecordingID.Error())
		sendResponseWithError(c, NewErrorNotFoundRecordingID())
		return
	}

	recorder.DelRecording(recordingID, "stop")

	sendResponseWithSuccess(c, nil)
}

// @Summary List Recordings
// @Description List Recordings
// @tags Recording
// @Produce json
// @Success 200 {object} response
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/recording [get]
func ListRecordings(c *gin.Context) {
	body := ListRecordingResponse{make([]string, 0)}
	list := recorder.ListRecordingIDs()
	logger.Debug("ListRecordings:", list)
	body.RecordingIDs = append(body.RecordingIDs, list...)

	sendResponseWithSuccess(c, body)
}
