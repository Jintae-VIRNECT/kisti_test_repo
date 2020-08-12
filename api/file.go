package api

import (
	"RM-RecordServer/data"
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"io"
	"os"
	"path/filepath"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
)

type ListRecordingFilesResponse struct {
	TotalPages  int                          `json:"totalPages,omitempty"`
	CurrentPage int                          `json:"currentPage,omitempty"`
	FileInfos   []recorder.RecordingFileInfo `json:"infos"`
}

type RemoveRecordingFilesResponse struct {
	Count int `json:"count"`
}

type Query struct {
	// search by filename. exact match and partial match is supported
	Filename string `json:"filename" example:"2020-08-14_12-00-00.mp4"`
	// search by recording_id
	ID string `json:"id" example:"recording_id"`
	// time-based search
	// - format: [op]:[date]
	// - date: ISO8601 format (YYYY-MM-DDTHH:mm:ss.sssZ)
	// - op:
	//   - ge: greater or equal (>=)
	//   - gt: greater (>)
	//   - le: little or equal (<=)
	//   - lt: little (<)
	// - example: /remote/recorder/file?createdAt=ge:2020-08-13T09:23:02Z&createdAt=le:2020-08-13T09:23:02Z"
	CreatedAt string `json:"createdAt" example:"ge:2020-08-13T09:23:02Z"`
	// the number of the page returned in the current request
	Page int `json:"page" example:"1"`
	// to define the number of items returned in the response
	Limit int `json:"limit" example:"5"`
	// to define the order of the returned response
	//  - format: [key].[asc|desc]
	//  - example: /remote/recorder/file?order=createdAt.asc
	Order string `json:"order" example:"createdAt.asc"`
}

// @Summary List Recording Files
// @Description List Recordings Files
// @tags Recording File
// @Param filename query Query false "description"
// @Produce json
// @Success 200 {object} response{data=ListRecordingFilesResponse}
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/file [get]
func ListRecordingFiles(c *gin.Context) {
	filter, err := setFilter(c)
	if err != nil {
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}
	logger.Debugf("filter: %+v", filter)

	list, totalPages, err := recorder.ListRecordingFiles(filter, false)
	if err != nil {
		logger.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}

	response := ListRecordingFilesResponse{FileInfos: list}
	if totalPages > 0 {
		response.TotalPages = totalPages
	}
	if filter.Page != nil {
		response.CurrentPage = *filter.Page
	}
	sendResponseWithSuccess(c, response)
}

func setFilter(c *gin.Context) (*data.Filter, error) {
	filter := &data.Filter{}

	if filename, ok := c.GetQuery("filename"); ok {
		filter.Filename = &filename
	}
	if id, ok := c.GetQuery("id"); ok {
		filter.RecordingID = &id
	}
	if page, ok := c.GetQuery("page"); ok {
		tmp, _ := strconv.Atoi(page)
		filter.Page = &tmp
	}
	if limit, ok := c.GetQuery("limit"); ok {
		tmp, _ := strconv.Atoi(limit)
		filter.Limit = &tmp
	}
	if sort, ok := c.GetQuery("order"); ok {
		tmp := strings.Join(strings.Split(sort, "."), " ")
		filter.OrderBy = &tmp
		logger.Debug("sort:", *filter.OrderBy)
	}
	if createdAt, ok := c.GetQueryArray("createdAt"); ok {
		logger.Debug("createdAt: ", createdAt)
		filter.CreatedAt = make([]data.FilterTime, 2)
		for _, t := range createdAt {
			tokens := strings.SplitN(t, ":", 2)
			logger.Debug(tokens)
			utc, err := time.Parse(time.RFC3339, tokens[1])
			if err != nil {
				return nil, err
			}
			logger.Debug(utc)

			filter.CreatedAt = append(filter.CreatedAt, data.FilterTime{Op: tokens[0], Time: utc})
		}
	}

	return filter, nil
}

// @Summary Remove All Recording Files
// @Description Remove All Recording Files
// @tags Recording File
// @Produce json
// @Success 200 {object} response{data=RemoveRecordingFilesResponse}
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/file [delete]
func RemoveRecordingFileAll(c *gin.Context) {
	count, err := recorder.RemoveRecordingFileAll()
	if err != nil {
		logger.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}
	sendResponseWithSuccess(c, RemoveRecordingFilesResponse{count})
}

// @Summary Remove Recording File
// @Description Remove Recording File
// @tags Recording File
// @Produce json
// @Param id path string true "recording id"
// @Success 200 {object} response
// @Failure 1000 {} json "{"error":"not found id"}"
// @Failure 9999 {} json "{"error":"error message"}"
// @Router /remote/recorder/file/{id} [delete]
func RemoveRecordingFile(c *gin.Context) {
	recordingID := c.Param("id")
	err := recorder.RemoveRecordingFile(recordingID)
	if err != nil {
		logger.Error(err)
		if err == recorder.ErrNotFoundRecordingID {
			sendResponseWithError(c, NewErrorNotFoundRecordingID())
		} else {
			sendResponseWithError(c, NewErrorInternalServer(err))
		}
		return
	}
	sendResponseWithSuccess(c, nil)
}

// @Summary Download Recording File
// @Description Download Recording File
// @tags Recording File
// @Produce json
// @Param id path string true "recording id"
// @Failure 1000 {} json "{"error":"not found id"}"
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
