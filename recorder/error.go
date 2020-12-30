package recorder

import "errors"

var (
	ErrRecordingIDAlreadyExists = errors.New("Recording ID Already Exists")
	ErrNotFoundRecordingID      = errors.New("Not Found Recording ID")
	ErrRecordingHasNotStarted   = errors.New("Recording Has Not Started")
	ErrInternalError            = errors.New("Internal Error")
)
