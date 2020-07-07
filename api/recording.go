package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"RM-RecordServer/util"
	"errors"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/spf13/viper"
)

type StartRecordingRequest struct {
	SessionID          string `json:"sessionId"`
	Resolution         string `json:"resolution,omitempty"`
	Framerate          uint   `json:"framerate,omitempty"`
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

type ListRecordingFilesResponse struct {
	Count     int                          `json:"numberOfInfos"`
	FileInfos []recorder.RecordingFileInfo `json:"infos"`
}

// @Summary Start Recording
// @Description Start Recording
// @Accept json
// @Produce json
// @Param body body StartRecordingRequest true "information for recording"
// @Success 200 {object} StartRecordingResponse
// @Failure 400 {} json "{"error":"error message"}"
// @Failure 429 {} json "{"error":"Too Many Recordings"}""
// @Failure 500 {} json "{"error":"error message"}"
// @Failure 507 {} json "{"error":"not enough free space"}"
// @Router /media/recorder/recording [post]
func StartRecording(c *gin.Context) {
	req := StartRecordingRequest{
		Resolution:         viper.GetString("record.defaultResolution"),
		Framerate:          viper.GetUint("record.defaultFramerate"),
		RecordingTimeLimit: viper.GetInt("record.defaultRecordingTimeLimit"),
	}
	err := c.BindJSON(&req)
	if err != nil {
		logger.Error("bind json fail:", err)
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}
	logger.Debugf("StartRecording:%+v", req)

	max := viper.GetInt("record.numOfConcurrentRecordings")
	cur := recorder.GetNumCurrentRecordings()
	if max <= cur {
		logger.Errorf("too many recording: current: %d limit:%d", cur, max)
		c.JSON(http.StatusTooManyRequests, gin.H{"error": "Too Many Recordings"})
		return
	}

	diskUsage, err := util.DiskUsage(viper.GetString("record.dir"))
	if float64(diskUsage.Free)/float64(util.GB) < viper.GetFloat64("record.diskFreeThreshold") {
		logger.Errorf("not enough free space: all:%f used:%f free:%f",
			float64(diskUsage.All)/float64(util.GB),
			float64(diskUsage.Used)/float64(util.GB),
			float64(diskUsage.Free)/float64(util.GB))
		c.JSON(http.StatusInsufficientStorage, gin.H{"error": "not enough free space"})
		return
	}

	err = checkValidation(&req)
	if err != nil {
		logger.Error(err)
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
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
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.Writer.WriteHeader(200)
}

func checkValidation(req *StartRecordingRequest) error {
	if req.SessionID == "" {
		return errors.New("sessionId is mandatory")
	}

	if req.RecordingTimeLimit < 5 || req.RecordingTimeLimit > 60 {
		return errors.New("recordingTimeLimit is invalid(min:5, max:60)")
	}

	if req.Framerate > 30 {
		return errors.New("framerate is invalid(max:30)")
	}

	resolution, err := convertResolution(req.Resolution)
	if err != nil {
		return err
	}
	req.Resolution = resolution

	return nil
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
// @Produce json
// @Param id path string true "recording id"
// @Success 200 {object} StopRecordingResponse
// @Failure 404 {} json "{ "error": "not found id" }"
// @Router /media/recorder/recording/{id} [delete]
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
// @Router /media/recorder/recordings [get]
func ListRecordings(c *gin.Context) {
	body := ListRecordingResponse{make([]string, 0)}
	list := recorder.ListRecordingIDs()
	logger.Debug("ListRecordings:", list)
	c.JSON(200, gin.H{
		"recordingIds": append(body.RecordingIDs, list...),
	})
}

// @Summary List Recording Files
// @Description List Recordings Files
// @Produce json
// @Success 200 {object} ListRecordingFilesResponse
// @Failure 500 {} json "{"error":"error message"}"
// @Router /media/recorder/files [get]
func ListRecordingFiles(c *gin.Context) {
	list, err := recorder.ListRecordingFiles()
	if err != nil {
		logger.Error(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	response := ListRecordingFilesResponse{FileInfos: list, Count: len(list)}
	c.JSON(200, response)
}
