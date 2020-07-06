package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/spf13/viper"
)

type StartRecordingRequest struct {
	SessionID          string `json:"sessionId"`
	Resolution         string `json:"resolution,omitempty"`
	Framerate          int    `json:"framerate,omitempty"`
	RecordingTimeLimit int    `json:"recordingTimeLimit,omitempty"`
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
// @Accept json
// @Produce json
// @Param body body StartRecordingRequest true "information for recording"
// @Success 200 {object} StartRecordingResponse
// @Failure 400 {} json "{"error":"error invalid parameter"}"
// @Failure 500 {} json "{"error":"error message"}"
// @Router /record [post]
func StartRecording(c *gin.Context) {
	req := StartRecordingRequest{
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

	exist := recorder.ExistRecordingID(req.SessionID)
	if exist == true {
		c.JSON(http.StatusBadRequest, gin.H{"error": recorder.ErrRecordingIDAlreadyExists.Error()})
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

	c.Writer.WriteHeader(200)
}

// @Summary Stop Recording
// @Description Stop Recording
// @Produce json
// @Param id path string true "recording id"
// @Success 200 {object} StopRecordingResponse
// @Failure 404 {} json "{ "error": "not found id" }"
// @Router /record/{id} [delete]
func StopRecording(c *gin.Context) {
	recordingID := c.Param("id")
	logger.Info("stop recording (id:", recordingID, ")")

	exist := recorder.ExistRecordingID(recordingID)
	if exist == false {
		c.JSON(http.StatusBadRequest, gin.H{"error": recorder.ErrNotFoundRecordingID.Error()})
		return
	}

	recorder.DelRecording(recordingID, "stop")

	c.Writer.WriteHeader(200)
}

// @Summary List Recordings
// @Description List Recordings
// @Produce json
// @Success 200 {object} ListRecordingResponse
// @Failure 500 {} json "{"error":"error message"}"
// @Router /records [get]
func ListRecordings(c *gin.Context) {
	body := ListRecordingResponse{make([]string, 0)}
	list := recorder.ListRecordingIDs()
	logger.Debug("ListRecordings:", list)
	c.JSON(200, gin.H{
		"recordingIds": append(body.RecordingIDs, list...),
	})
}
