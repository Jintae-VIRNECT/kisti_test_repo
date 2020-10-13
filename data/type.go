package data

type WorkspaceID string

func (id WorkspaceID) String() string {
	return string(id)
}

type RecordingID string

func (id RecordingID) String() string {
	return string(id)
}

type SessionID string

func (id SessionID) String() string {
	return string(id)
}
