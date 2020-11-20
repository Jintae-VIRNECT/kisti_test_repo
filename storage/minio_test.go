package storage_test

import (
	"RM-RecordServer/data"
	"RM-RecordServer/logger"
	"RM-RecordServer/storage"
	"context"
	"testing"

	"github.com/sirupsen/logrus"
	"github.com/stretchr/testify/assert"
)

func createContext() context.Context {
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	return context.WithValue(context.Background(), data.ContextKeyLog, logEntry)
}

func TestGetObjectURLS3(t *testing.T) {
	urlString := "https://virnect-remote.s3.amazonaws.com/recordings/aaa/bbb/cccc.mp4"
	target := "aaa/bbb/cccc.mp4"
	c := &storage.Client{
		Endpoint:     "s3.amazonaws.com",
		UseSSL:       true,
		BucketName:   "virnect-remote",
		ResourceName: "recordings",
	}

	downloadUrl, err := c.GetObjectUrl(createContext(), target)
	assert.Nil(t, err)

	assert.Equal(t, downloadUrl, urlString)
}

func TestGetObjectURL(t *testing.T) {
	urlString := "https://127.0.0.1:12345/virnect-remote/recordings/aaa/bbb/cccc.mp4"
	target := "aaa/bbb/cccc.mp4"
	c := &storage.Client{
		Endpoint:     "127.0.0.1:12345",
		UseSSL:       true,
		BucketName:   "virnect-remote",
		ResourceName: "recordings",
	}

	downloadUrl, err := c.GetObjectUrl(createContext(), target)
	assert.Nil(t, err)

	assert.Equal(t, downloadUrl, urlString)
}
