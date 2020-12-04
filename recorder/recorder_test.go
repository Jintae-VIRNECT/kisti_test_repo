package recorder

import (
	"RM-RecordServer/data"
	"fmt"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
)

type IDs struct {
	recordingID data.RecordingID
	sessionID   data.SessionID
	workspaceID data.WorkspaceID
}

func addRecording(r *recorder, count int) []IDs {
	ids := []IDs{}
	for i := 0; i < count; i++ {
		recordingID := data.RecordingID(fmt.Sprintf("r.%d", i))
		sessionID := data.SessionID(fmt.Sprintf("s.%d", i))
		workspaceID := data.WorkspaceID(fmt.Sprintf("w.%d", i))
		userID := "user-1"
		containerID := "container-1"
		createTime := time.Now()
		timeLimit := 60

		r.addRecording(
			recordingID,
			sessionID,
			workspaceID,
			userID,
			containerID,
			createTime,
			timeLimit,
			nil,
		)

		ids = append(ids, IDs{recordingID, sessionID, workspaceID})
	}
	return ids
}

func TestAddRecording(t *testing.T) {
	r := newRecorder()

	recordingID := data.RecordingID("recording-1")
	sessionID := data.SessionID("session-1")
	workspaceID := data.WorkspaceID("workspace-1")
	userID := "user-1"
	containerID := "container-1"
	createTime := time.Now()
	timeLimit := 60

	recording, err := r.addRecording(
		recordingID,
		sessionID,
		workspaceID,
		userID,
		containerID,
		createTime,
		timeLimit,
		nil,
	)
	assert.Nil(t, err)
	assert.NotNil(t, recording)

	count := r.size()
	assert.Equal(t, count, 1)

	recording, err = r.addRecording(
		recordingID,
		sessionID,
		workspaceID,
		userID,
		containerID,
		createTime,
		timeLimit,
		nil,
	)
	assert.Equal(t, err, ErrRecordingIDAlreadyExists)
}

func TestRemoveRecording(t *testing.T) {
	r := newRecorder()

	ids := addRecording(r, 1)

	recording := r.removeRecording(ids[0].recordingID, ids[0].workspaceID)
	assert.NotNil(t, recording)

	count := r.size()
	assert.Equal(t, count, 0)
	assert.Equal(t, recording.id, ids[0].recordingID)
}

func TestFindRecording(t *testing.T) {
	r := newRecorder()

	count := 10
	ids := addRecording(r, count)

	// find all
	res := r.findRecordings(nil, nil, nil)
	assert.Equal(t, len(res), count)

	// find by RecordingID
	res = r.findRecordings(&ids[0].recordingID, nil, nil)
	assert.Equal(t, len(res), 1)
	assert.Equal(t, res[0].id, ids[0].recordingID)

	// find by WorkspaceID
	res = r.findRecordings(nil, &ids[0].workspaceID, nil)
	assert.Equal(t, len(res), 1)
	assert.Equal(t, res[0].workspaceID, ids[0].workspaceID)

	// find by SessionID
	res = r.findRecordings(nil, nil, &ids[0].sessionID)
	assert.Equal(t, len(res), 1)
	assert.Equal(t, res[0].sessionID, ids[0].sessionID)

	// not found id
	recordingID := data.RecordingID("r1234")
	res = r.findRecordings(&recordingID, nil, nil)
	assert.Equal(t, len(res), 0)
}
