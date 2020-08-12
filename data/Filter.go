package data

import "time"

type FilterTime struct {
	Op   string
	Time time.Time
}

type Filter struct {
	RecordingID *string
	Filename    *string
	CreatedAt   []FilterTime
	Page        *int
	Limit       *int
	OrderBy     *string
}
