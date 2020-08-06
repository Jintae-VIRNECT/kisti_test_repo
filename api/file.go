package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"io"
	"net/http"
	"os"
	"path/filepath"

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

// @Summary Download Recording File
// @Description Download Recording File
// @tags Recording File
// @Produce json
// @Param id path string true "recording id"
// @Failure 404 {} json "{ "error": "not found id" }"
// @Failure 500 {} json "{"error":"error message"}"
// @Router /remote/recorder/file/download/{id} [get]
func DownloadRecordingFile(c *gin.Context) {
	recordingID := c.Param("id")

	filePath, err := recorder.GetRecordingFilePath(recordingID)
	if err != nil {
		c.JSON(http.StatusNotFound, err.Error())
		return
	}
	logger.Debug("file:", filePath)

	file, err := os.Open(filePath) // open a file
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	defer file.Close()

	c.Header("Content-Description", "File Transfer")
	c.Header("Content-Transfer-Encoding", "binary")
	c.Header("Content-type", "application/octet-stream")
	c.Header("Content-Disposition", "attachment; filename="+filepath.Base(filePath))

	logger.Info("begin download:", filePath)
	_, err = io.Copy(c.Writer, file)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}
	logger.Info("end download:", filePath)
}
