package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/database"
	"context"
	"encoding/json"
	"errors"
	"path/filepath"

	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

var db *database.RecordingFileDB

func insertIntoDB(ctx context.Context, recordingID string) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	if db == nil {
		return
	}
	root := viper.GetString("record.dir")
	info, err := readInfoFile(ctx, recordingID, filepath.Join(root, recordingID))
	if err != nil {
		log.Error(err)
		return
	}
	metaData, _ := json.Marshal(info.MetaData)
	if _, err := db.Create(
		recordingID,
		info.SessionID,
		info.Filename,
		info.FullPath,
		info.Duration,
		info.Size,
		info.Resolution,
		info.Framerate,
		string(metaData),
		info.CreateAt,
	); err != nil {
		log.Error(err)
	}
}

func deleteOnDB(ctx context.Context, filter *data.Filter) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	if db == nil {
		return
	}
	if _, err := db.Delete(filter); err != nil {
		log.Error(err)
	}
}

func queryFromDB(ctx context.Context, filter *data.Filter) ([]RecordingFileInfo, int, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	infos := []RecordingFileInfo{}

	if db == nil {
		return infos, 0, errors.New("db is null")
	}

	records, totalPages, err := db.Select(filter)
	if err != nil {
		log.Error(err)
		return infos, 0, err
	}

	for _, r := range records {
		info := RecordingFileInfo{
			RecordingID: r.RecordingID,
			SessionID:   r.SessionID,
			Filename:    r.Filename,
			FullPath:    r.FullPath,
			Duration:    r.Duration,
			Size:        r.Size,
			Resolution:  r.Resolution,
			Framerate:   r.Framerate,
			CreateAt:    r.CreatedAt,
			MetaData:    r.MetaData,
		}
		log.Debug("CreateAt", r.CreatedAt)
		infos = append(infos, info)
	}

	return infos, totalPages, nil
}
