package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/util"
	"context"
	"encoding/json"
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"
	"syscall"
	"time"

	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

func ListRecordingFiles(ctx context.Context, filter *data.Filter, onlyLocalStorage bool) ([]RecordingFileInfo, int, error) {
	if db != nil && onlyLocalStorage == false {
		return queryFromDB(ctx, filter)
	} else {
		infos, err := queryFromLocal(ctx)
		return infos, 0, err
	}
}

func RemoveRecordingFileAll(ctx context.Context) (int, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	deleteOnDB(ctx, nil)

	count, err := util.RemoveContents(viper.GetString("record.dir"))
	log.Info("delete all recording files. count:", count)

	return count, err
}

func RemoveRecordingFile(ctx context.Context, recordingID string) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	filter := &data.Filter{RecordingID: &recordingID}
	deleteOnDB(ctx, filter)

	filePath, err := GetRecordingFilePath(ctx, recordingID)
	if err != nil {
		return ErrNotFoundRecordingID
	}
	log.Infof("delete recording id:%s file:%s", recordingID, filePath)

	return removeFile(ctx, filePath)
}

func GetRecordingFilePath(ctx context.Context, recordingID string) (string, error) {
	root := viper.GetString("record.dir")
	info, err := readInfoFile(ctx, recordingID, filepath.Join(root, recordingID))
	if err != nil {
		return "", err
	}

	return info.FullPath, nil
}

func queryFromLocal(ctx context.Context) ([]RecordingFileInfo, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	infos := []RecordingFileInfo{}

	root := viper.GetString("record.dir")
	if _, err := os.Stat(root); os.IsNotExist(err) {
		log.Error(err)
		return infos, err
	}

	recDirs, err := ioutil.ReadDir(root)
	if err != nil {
		return infos, err
	}

	for _, recDir := range recDirs {
		recordingID := recDir.Name()
		if ExistRecordingID(recordingID) {
			continue
		}
		info, err := readInfoFile(ctx, recordingID, filepath.Join(root, recordingID))
		if err != nil {
			if os.IsNotExist(err) {
				log.Debug("skip file:", recordingID, "(empty directory)")
			} else {
				log.Warn(err, " file:", recordingID)
			}
			continue
		}
		infos = append(infos, info)
	}

	return infos, nil
}

func removeFile(ctx context.Context, file string) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Info("delete recording file:", filepath.Dir(file))

	err := os.RemoveAll(filepath.Dir(file))
	if err != nil {
		log.Error("delete fail. ", err)
		return err
	}

	return nil
}

func readInfoFile(ctx context.Context, recordingID string, path string) (RecordingFileInfo, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	info := RecordingFileInfo{}

	infoFile, err := os.Open(filepath.Join(path, ".recording."+recordingID))
	if err != nil {
		log.Error("open file:", err)
		return info, err
	}
	defer infoFile.Close()
	byteValue, err := ioutil.ReadAll(infoFile)
	if err != nil {
		log.Error("json parse file:", err)
		return info, err
	}
	var result map[string]interface{}
	json.Unmarshal([]byte(byteValue), &result)

	if _, ok := result["recordingId"]; !ok {
		result["recordingId"] = recordingID
	}

	if _, ok := result["sessionId"]; !ok {
		result["sessionId"] = ""
	}

	if _, ok := result["filename"]; !ok {
		result["filename"] = ""
	}

	if _, ok := result["duration"]; !ok {
		result["duration"] = 0.0
	}

	if _, ok := result["size"]; !ok {
		result["size"] = 0.0
	}

	if _, ok := result["resolution"]; !ok {
		result["resolution"] = ""
	}

	if _, ok := result["framerate"]; !ok {
		result["framerate"] = 0.0
	}

	filenameWithPath := result["filename"].(string)
	fullPath := filepath.Join(viper.GetString("record.dir"), strings.TrimPrefix(filenameWithPath, viper.GetString("record.dirOnDocker")))

	finfo, err := os.Stat(path)
	if err != nil {
		return info, err
	}
	stat := finfo.Sys().(*syscall.Stat_t)
	ts := stat.Ctim

	info.RecordingID = result["recordingId"].(string)
	info.SessionID = result["sessionId"].(string)
	info.Filename = filepath.Base(filenameWithPath)
	info.FullPath = fullPath
	info.Duration = int(result["duration"].(float64))
	info.Size = int(result["size"].(float64))
	info.Resolution = result["resolution"].(string)
	info.Framerate = uint(result["framerate"].(float64))
	info.CreateAt = time.Unix(int64(ts.Sec), int64(ts.Nsec)).UTC()
	if metaData, ok := result["metaData"]; ok == true {
		info.MetaData = metaData.(interface{})
	}

	return info, nil
}
