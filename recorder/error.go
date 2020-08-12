package recorder

import "errors"

var (
	ErrRecordingIDAlreadyExists = errors.New("Recording ID Already Exists")
	ErrNotFoundRecordingID      = errors.New("Not Found Recording ID")
	ErrInternalError            = errors.New("Internal Error")
)
