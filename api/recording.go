package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"net/http"

	"github.com/spf13/viper"

	"github.com/gin-gonic/gin"
)

type startRecordingReqBody struct {
	SessionID          string `json:"sessionId"`
	Resolution         string `json:"resolution,omitempty"`
	Framerate          int    `json:"framerate,omitempty"`
	RecordingTimeLimit int    `json:"recordingTimeLimit,omitempty"`
}

type startRecordingResBody struct {
	SessionID string `json:"sessionId"`
}

type stopRecordingResBody struct {
	SessionID string `json:"sessionId"`
	Filename  string `json:"filename"`
	Duration  int    `json:"duration"`
}

type listRecordingResBody struct {
	SessionIds []string `json:"sessionIds"`
}

// @Summary Start Recording
// @Description Start Recording
// @Accept json
// @Produce json
// @Param sessionId body startRecordingReqBody true "information for recording"
// @Param RecordingTimeLimit body startRecordingReqBody true "information for recording"
// @Param Resolution body startRecordingReqBody true "information for recording"
// @Success 200 {object} startRecordingResBody	"ok"
// @Failure 400 {string} Error "We need ID!!"
// @Failure 404 {string} Error "Can not find ID"
// @Router /record [post]
func StartRecording(c *gin.Context) {
	req := startRecordingReqBody{
		Resolution:         viper.GetString("record.defaultResolution"),
		Framerate:          viper.GetInt("record.defaultFramerate"),
		RecordingTimeLimit: viper.GetInt("record.defaultRecordingTimeLimit"),
	}
	err := c.BindJSON(&req)
	if err != nil {
		logger.Error("bind json fail:", err)
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	logger.Debugf("StartRecording:%+v", req)

	if req.SessionID == "" {
		logger.Error("empty sessionId")
		c.JSON(http.StatusBadRequest, gin.H{"error": "sessionId is mandatory"})
		return
	}

	exist := recorder.ExistSessionID(req.SessionID)
	if exist == true {
		c.JSON(http.StatusBadRequest, gin.H{"error": recorder.ErrSessionIDAlreadyExists.Error()})
		return
	}

	param := recorder.RecordingParam{
		SessionID:          req.SessionID,
		Resolution:         req.Resolution,
		Framerate:          req.Framerate,
		RecordingTimeLimit: req.RecordingTimeLimit,
	}

	err = recorder.NewRecording(param)
	if err != nil {
		logger.Error(err)
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"response": "ok",
	})
}

// @Summary Stop Recording
// @Description Stop Recording
// @Produce json
// @Param sessionId path string true "recording id"
// @Success 200 {object} stopRecordingResBody
// @Failure 400 {string} Error "We need ID!!"
// @Failure 404 {string} Error "Can not find ID"
// @Router /record/{sessionId} [delete]
func StopRecording(c *gin.Context) {
	sessionID := c.Param("id")
	logger.Info("stop recording (sessionId:", sessionID, ")")

	exist := recorder.ExistSessionID(sessionID)
	if exist == false {
		c.JSON(http.StatusBadRequest, gin.H{"error": recorder.ErrNotFoundSessionID.Error()})
		return
	}

	recorder.DelRecording(sessionID, "stop")

	c.JSON(200, gin.H{
		"duration": 10,
	})
}

// @Summary List Recordings
// @Description List Recordings
// @Produce json
// @Success 200 {object} listRecordingResBody
// @Failure 400 {string} Error "We need ID!!"
// @Failure 404 {string} Error "Can not find ID"
// @Router /records [get]
func ListRecordings(c *gin.Context) {
	result := make([]string, 0)
	list := recorder.ListSessionIDs()
	logger.Debug("ListRecordings:", list)
	c.JSON(200, gin.H{
		"recordings": append(result, list...),
	})
}
