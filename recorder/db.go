package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/database"
	"RM-RecordServer/logger"
	"encoding/json"
	"errors"
	"path/filepath"

	"github.com/spf13/viper"
)

var db *database.RecordingFileDB

func insertIntoDB(recordingID string) {
	if db == nil {
		return
	}
	root := viper.GetString("record.dir")
	info, err := readInfoFile(recordingID, filepath.Join(root, recordingID))
	if err != nil {
		logger.Error(err)
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
		logger.Error(err)
	}
}

func deleteOnDB(filter *data.Filter) {
	if db == nil {
		return
	}
	if _, err := db.Delete(filter); err != nil {
		logger.Error(err)
	}
}

func queryFromDB(filter *data.Filter) ([]RecordingFileInfo, int, error) {
	infos := []RecordingFileInfo{}

	if db == nil {
		return infos, 0, errors.New("db is null")
	}

	records, totalPages, err := db.Select(filter)
	if err != nil {
		logger.Error(err)
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
		logger.Debug("CreateAt", r.CreatedAt)
		infos = append(infos, info)
	}

	return infos, totalPages, nil
}
