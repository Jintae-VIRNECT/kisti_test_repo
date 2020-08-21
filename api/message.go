package api

import (
	"github.com/gin-gonic/gin"
	"github.com/spf13/viper"
)

const (
	ErrNotFoundRecordingID     = 1000
	ErrTooManyRecordings       = 1001
	ErrInsufficientStorage     = 1002
	ErrInvalidRequestParameter = 8001
	ErrInternalServer          = 9999
)

type responseError struct {
	code    int
	message string
}

type successResponse struct {
	Data    interface{} `json:"data"`
	Code    int         `json:"code"`
	Message string      `json:"message"`
}

type failResponse struct {
	Data    interface{} `json:"data"`
	Service string      `json:"service"`
	Code    int         `json:"code"`
	Message string      `json:"message"`
}

type empty struct {
}

func NewErrorNotFoundRecordingID() *responseError {
	return &responseError{code: ErrNotFoundRecordingID, message: "Not Found Recording ID"}
}

func NewErrorTooManyRecordings() *responseError {
	return &responseError{code: ErrTooManyRecordings, message: "Too Many Recordings"}
}

func NewErrorInsufficientStorage() *responseError {
	return &responseError{code: ErrInsufficientStorage, message: "Not Enough Free Space"}
}

func NewErrorInvalidRequestParameter(err error) *responseError {
	return &responseError{code: ErrInvalidRequestParameter, message: err.Error()}
}

func NewErrorInternalServer(err error) *responseError {
	return &responseError{code: ErrInternalServer, message: err.Error()}
}

func sendResponseWithSuccess(c *gin.Context, data interface{}) {
	response := &successResponse{
		Data:    data,
		Code:    200,
		Message: "success",
	}

	if response.Data == nil {
		response.Data = &empty{}
	}

	c.JSON(200, response)
}

func sendResponseWithError(c *gin.Context, err *responseError) {
	service := viper.GetString("general.service")
	response := &failResponse{
		Data:    empty{},
		Service: service,
		Code:    err.code,
		Message: err.message,
	}
	c.JSON(200, response)
}
