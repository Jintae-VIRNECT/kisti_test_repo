package api

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"io"
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
// @Success 200 {object} response
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/file [get]
func ListRecordingFiles(c *gin.Context) {
	list, err := recorder.ListRecordingFiles()
	if err != nil {
		logger.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}
	sendResponseWithSuccess(c, ListRecordingFilesResponse{FileInfos: list, Count: len(list)})
}

// @Summary Remove All Recording Files
// @Description Remove All Recordings Files
// @tags Recording File
// @Produce json
// @Success 200 {object} response
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/file [delete]
func RemoveRecordingFiles(c *gin.Context) {
	count, err := recorder.RemoveRecordingFiles()
	if err != nil {
		logger.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}
	sendResponseWithSuccess(c, RemoveRecordingFilesResponse{count})
}

// @Summary Download Recording File
// @Description Download Recording File
// @tags Recording File
// @Produce json
// @Param id path string true "recording id"
// @Failure 1000 {} json "{ "error": "not found id" }"
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/file/download/{id} [get]
func DownloadRecordingFile(c *gin.Context) {
	recordingID := c.Param("id")

	filePath, err := recorder.GetRecordingFilePath(recordingID)
	if err != nil {
		sendResponseWithError(c, NewErrorNotFoundRecordingID())
		return
	}
	logger.Debug("file:", filePath)

	file, err := os.Open(filePath) // open a file
	if err != nil {
		sendResponseWithError(c, NewErrorInternalServer(err))
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
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}
	logger.Info("end download:", filePath)
}
