package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/database"
	"context"
	"encoding/json"

	"github.com/sirupsen/logrus"
)

var db *database.RecordingFileDB

func insertIntoDB(ctx context.Context, info RecordingFileInfo) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	metaData, _ := json.Marshal(info.MetaData)
	if _, err := db.Create(
		info.RecordingID,
		info.SessionID,
		info.WorkspaceID,
		info.UserID,
		info.Filename,
		info.Duration,
		info.Size,
		info.Resolution,
		info.Framerate,
		string(metaData),
		info.CreateAt,
	); err != nil {
		log.Error(err)
		return err
	}
	return nil
}

func deleteOnDB(ctx context.Context, filter *data.Filter) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	if _, err := db.Delete(filter); err != nil {
		log.Error(err)
	}
}

func queryFromDB(ctx context.Context, filter *data.Filter) ([]RecordingFileInfo, int, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	infos := []RecordingFileInfo{}

	records, totalPages, err := db.Select(filter)
	if err != nil {
		log.Error(err)
		return infos, 0, err
	}

	for _, r := range records {
		info := RecordingFileInfo{
			RecordingID: r.RecordingID,
			SessionID:   r.SessionID,
			WorkspaceID: r.WorkspaceID,
			UserID:      r.UserID,
			Filename:    r.Filename,
			Duration:    r.Duration,
			Size:        r.Size,
			Resolution:  r.Resolution,
			Framerate:   r.Framerate,
			CreateAt:    r.CreatedAt,
			MetaData:    r.MetaData,
		}
		log.Debug("CreateAt:", r.CreatedAt)
		infos = append(infos, info)
	}

	return infos, totalPages, nil
}
