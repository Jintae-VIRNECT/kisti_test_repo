package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"net/http"

	"github.com/gin-gonic/gin"
)

type ListRecordingFilesResponse struct {
	Count     int                          `json:"numberOfInfos"`
	FileInfos []recorder.RecordingFileInfo `json:"infos"`
}

type RemoveRecordingFilesResponse struct {
	Count int `json:"count"`
}

// @Summary List Recording Files
// @Description List Recordings Files
// @tags Recording File
// @Produce json
// @Success 200 {object} ListRecordingFilesResponse
// @Failure 500 {} json "{"error":"error message"}"
// @Router /remote/recorder/file [get]
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

// @Summary Remove All Recording Files
// @Description Remove All Recordings Files
// @tags Recording File
// @Produce json
// @Success 200 {object} RemoveRecordingFilesResponse
// @Failure 500 {} json "{"error":"error message"}"
// @Router /remote/recorder/file [delete]
func RemoveRecordingFiles(c *gin.Context) {
	count, err := recorder.RemoveRecordingFiles()
	if err != nil {
		logger.Error(err)
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	response := RemoveRecordingFilesResponse{count}
	c.JSON(200, response)
}
