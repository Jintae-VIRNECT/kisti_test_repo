package api

import (
	"RM-RecordServer/data"
	"RM-RecordServer/recorder"
	"RM-RecordServer/util"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

type RecordingFiles struct {
	RecordingID data.RecordingID `json:"recordingId"`
	SessionID   data.SessionID   `json:"sessionId"`
	WorkspaceID data.WorkspaceID `json:"workspaceId"`
	UserID      string           `json:"userId"`
	Filename    string           `json:"filename"`
	Duration    int              `json:"duration"`
	Size        int              `json:"size"`
	Resolution  string           `json:"resolution"`
	Framerate   int              `json:"framerate"`
	CreateAt    time.Time        `json:"createAt"`
	MetaData    interface{}      `json:"metaData,omitempty"`
}

type ListRecordingFilesResponse struct {
	TotalPages  int              `json:"totalPages,omitempty"`
	CurrentPage int              `json:"currentPage,omitempty"`
	FileInfos   []RecordingFiles `json:"infos"`
}

type RemoveRecordingFilesResponse struct {
	Count int `json:"count"`
}

type ListRecordingFilesQuery struct {
	// search by filename. exact match and partial match is supported
	Filename string `json:"filename" example:"2020-08-14_12-00-00.mp4"`
	// search by recording_id
	ID string `json:"id" example:"recording_id"`
	// search by session_id
	SessionID string `json:"sessionId" example:"session_id"`
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
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param filename query ListRecordingFilesQuery false "description"
// @Produce json
// @Success 200 {object} successResponse{data=ListRecordingFilesResponse}
// @Failure 9999 {} json "{"code":9999,"message":"error message","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/files [get]
func ListRecordingFiles(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)
	filter, err := setFilter(c)
	if err != nil {
		sendResponseWithError(c, NewErrorInvalidRequestParameter(err))
		return
	}
	log.Debugf("filter: %+v", filter)

	list, totalPages, err := recorder.ListRecordingFiles(c.Request.Context(), filter)
	if err != nil {
		log.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}

	fileInfos := []RecordingFiles{}
	for _, f := range list {
		fileInfos = append(fileInfos, RecordingFiles{
			RecordingID: f.RecordingID,
			SessionID:   f.SessionID,
			WorkspaceID: f.WorkspaceID,
			UserID:      f.UserID,
			Filename:    f.Filename,
			Duration:    f.Duration,
			Size:        f.Size,
			Resolution:  f.Resolution,
			Framerate:   f.Framerate,
			CreateAt:    f.CreateAt,
			MetaData:    f.MetaData,
		})
	}

	response := ListRecordingFilesResponse{FileInfos: fileInfos}
	if totalPages > 0 {
		response.TotalPages = totalPages
	}
	if filter.Page != nil {
		response.CurrentPage = *filter.Page
	}
	sendResponseWithSuccess(c, response)
}

func setFilter(c *gin.Context) (*data.Filter, error) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)
	filter := &data.Filter{}

	// parameter in path
	workspaceID := data.WorkspaceID(c.Param("workspaceId"))
	filter.WorkspaceID = &workspaceID

	// query
	if filename, ok := c.GetQuery("filename"); ok {
		filter.Filename = &filename
	}
	if id, ok := c.GetQuery("id"); ok {
		tmp := data.RecordingID(id)
		filter.RecordingID = &tmp
	}
	if id, ok := c.GetQuery("sessionId"); ok {
		tmp := data.SessionID(id)
		filter.SessionID = &tmp
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
		tokens := strings.Split(sort, ".")
		orderBy := strings.Join([]string{util.ToSnakeCase(tokens[0]), tokens[1]}, " ")
		filter.OrderBy = &orderBy
		log.Debug("sort:", *filter.OrderBy)
	}
	if createdAt, ok := c.GetQueryArray("createdAt"); ok {
		log.Debug("createdAt: ", createdAt)
		filter.CreatedAt = make([]data.FilterTime, 2)
		for _, t := range createdAt {
			tokens := strings.SplitN(t, ":", 2)
			log.Debug(tokens)
			utc, err := time.Parse(time.RFC3339, tokens[1])
			if err != nil {
				return nil, err
			}
			log.Debug(utc)

			filter.CreatedAt = append(filter.CreatedAt, data.FilterTime{Op: tokens[0], Time: utc})
		}
	}

	return filter, nil
}

// @Summary Remove All Recording Files
// @Description Remove All Recording Files
// @tags Recording File
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Success 200 {object} successResponse{data=RemoveRecordingFilesResponse}
// @Failure 9999 {} json "{"code":9999,"message":"error message","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/files [delete]
func RemoveRecordingFileAll(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)
	workspaceID := data.WorkspaceID(c.Param("workspaceId"))
	count, err := recorder.RemoveRecordingFileAll(c.Request.Context(), workspaceID)
	if err != nil {
		log.Error(err)
		sendResponseWithError(c, NewErrorInternalServer(err))
		return
	}
	sendResponseWithSuccess(c, RemoveRecordingFilesResponse{count})
}

// @Summary Remove Recording File
// @Description Remove Recording File
// @tags Recording File
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param id path string true "recording id"
// @Success 200 {object} successResponse
// @Failure 1000 {} json "{"code":1000,"message":"Not Found ID","service":"remote-record-server","data":{}}"
// @Failure 9999 {} json "{"code":9999,"message":"error message","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/files/{id} [delete]
func RemoveRecordingFile(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)

	workspaceID := data.WorkspaceID(c.Param("workspaceId"))
	recordingID := data.RecordingID(c.Param("id"))
	filter := &data.Filter{WorkspaceID: &workspaceID, RecordingID: &recordingID}

	err := recorder.RemoveRecordingFile(c.Request.Context(), filter)
	if err != nil {
		log.Error(err)
		if err == recorder.ErrNotFoundRecordingID {
			sendResponseWithError(c, NewErrorNotFoundRecordingID())
		} else {
			sendResponseWithError(c, NewErrorInternalServer(err))
		}
		return
	}
	sendResponseWithSuccess(c, nil)
}

// @Summary Get Recording File Download Url
// @Description Get Recording File Download Url
// @tags Recording File
// @Produce json
// @Param workspaceId path string true "workspace id"
// @Param userId path string true "user id"
// @Param id path string true "recording id"
// @Success 200 {} json "{"code":200,"message":"success","data":"download_url"}"
// @Failure 1000 {} json "{"code":1000,"message":"Not Found ID","service":"remote-record-server","data":{}}"
// @Failure 9999 {} json "{"code":9999,"message":"error message","service":"remote-record-server","data":{}}"
// @Router /remote/recorder/workspaces/{workspaceId}/users/{userId}/files/{id}/url [get]
func GetRecordingFileDownloadUrl(c *gin.Context) {
	log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)

	workspaceID := data.WorkspaceID(c.Param("workspaceId"))
	recordingID := data.RecordingID(c.Param("id"))
	filter := &data.Filter{WorkspaceID: &workspaceID, RecordingID: &recordingID}

	url, err := recorder.GetRecordingFileDownloadUrl(c.Request.Context(), filter)
	if err != nil {
		log.Error(err)
		if err == recorder.ErrNotFoundRecordingID {
			sendResponseWithError(c, NewErrorNotFoundRecordingID())
		} else {
			sendResponseWithError(c, NewErrorInternalServer(err))
		}
		return
	}

	sendResponseWithSuccess(c, url)
}
