package data

import "time"

type FilterTime struct {
	Op   string
	Time time.Time
}

type Filter struct {
	RecordingID *RecordingID
	SessionID   *SessionID
	WorkspaceID *WorkspaceID
	Filename    *string
	CreatedAt   []FilterTime
	Page        *int
	Limit       *int
	OrderBy     *string
}
