package recorder

import (
	"RM-RecordServer/data"
	"RM-RecordServer/storage"
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

type RecordingFileInfo struct {
	RecordingID data.RecordingID
	SessionID   data.SessionID
	WorkspaceID data.WorkspaceID
	UserID      string
	Filename    string
	FullPath    string
	Duration    int
	Size        int
	Resolution  string
	Framerate   int
	CreateAt    time.Time
	MetaData    interface{}
}

func getStoragePath(info RecordingFileInfo) string {
	return strings.Join([]string{info.WorkspaceID.String(), info.SessionID.String(), info.RecordingID.String(), info.Filename}, "/")
}

func ListRecordingFiles(ctx context.Context, filter *data.Filter) ([]RecordingFileInfo, int, error) {
	return queryFromDB(ctx, filter)
}

func ListRecordingFilesOnLocalStorage(ctx context.Context) ([]RecordingFileInfo, error) {
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
		recordingID := data.RecordingID(recDir.Name())
		recordings := mainRecorder.findRecordings(&recordingID, nil, nil)
		if len(recordings) > 0 {
			log.Debugf("skip file: recordingId:%s is now recording", recordingID)
			continue
		}

		info, err := readInfoFile(ctx, recordingID)
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

func RemoveRecordingFileAll(ctx context.Context, workspaceID data.WorkspaceID) (int, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	filter := &data.Filter{WorkspaceID: &workspaceID}
	infos, _, err := queryFromDB(ctx, filter)

	for _, info := range infos {
		log.Infof("delete file. recordingId:%s filename:%s", info.RecordingID, info.Filename)
		storagePath := getStoragePath(info)
		err = storage.NewClient().Remove(ctx, storagePath)
		if err != nil {
			log.Warn(err)
		}
	}

	deleteOnDB(ctx, filter)

	log.Info("delete all recording files. count:", len(infos))
	return len(infos), err
}

func RemoveRecordingFile(ctx context.Context, filter *data.Filter) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	infos, _, err := queryFromDB(ctx, filter)
	if err != nil {
		return ErrInternalError
	}

	if len(infos) == 0 {
		return ErrNotFoundRecordingID
	}

	storagePath := getStoragePath(infos[0])
	err = storage.NewClient().Remove(ctx, storagePath)
	if err != nil {
		log.Warn(err)
	}

	deleteOnDB(ctx, filter)

	log.Infof("delete recording id:%s, filename:%s", infos[0].RecordingID, infos[0].Filename)
	return nil
}

func GetRecordingFileDownloadUrl(ctx context.Context, filter *data.Filter) (string, error) {
	infos, _, err := queryFromDB(ctx, filter)
	if err != nil {
		return "", ErrInternalError
	}

	if len(infos) == 0 {
		return "", ErrNotFoundRecordingID
	}

	target := getStoragePath(infos[0])
	url, err := storage.NewClient().GetObjectUrl(ctx, target, infos[0].Filename)
	if err != nil {
		return "", err
	}

	return url, nil
}

func removeLocalFile(ctx context.Context, file string) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	log.Info("delete recording file:", filepath.Dir(file))

	err := os.RemoveAll(filepath.Dir(file))
	if err != nil {
		log.Error("delete fail. ", err)
		return err
	}

	return nil
}

func readInfoFile(ctx context.Context, recordingID data.RecordingID) (RecordingFileInfo, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	info := RecordingFileInfo{}
	path := filepath.Join(viper.GetString("record.dir"), string(recordingID))
	infoFile, err := os.Open(filepath.Join(path, ".recording."+string(recordingID)))
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

	if _, ok := result["workspaceId"]; !ok {
		result["workspaceId"] = ""
	}

	if _, ok := result["userId"]; !ok {
		result["userId"] = ""
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

	info.RecordingID = data.RecordingID(result["recordingId"].(string))
	info.SessionID = data.SessionID(result["sessionId"].(string))
	info.WorkspaceID = data.WorkspaceID(result["workspaceId"].(string))
	info.UserID = result["userId"].(string)
	info.Filename = filepath.Base(filenameWithPath)
	info.FullPath = fullPath
	info.Duration = int(result["duration"].(float64))
	info.Size = int(result["size"].(float64))
	info.Resolution = result["resolution"].(string)
	info.Framerate = int(result["framerate"].(float64))
	info.CreateAt = time.Unix(int64(ts.Sec), int64(ts.Nsec)).UTC()
	if metaData, ok := result["metaData"]; ok == true {
		info.MetaData = metaData.(interface{})
	}

	return info, nil
}

func upload(ctx context.Context, info RecordingFileInfo) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)
	storagePath := getStoragePath(info)
	if err := storage.NewClient().Upload(ctx, info.FullPath, storagePath); err != nil {
		log.Error(err)
		return err
	}
	if err := insertIntoDB(ctx, info); err != nil {
		log.Error(err)
		if err = storage.NewClient().Remove(ctx, storagePath); err != nil {
			log.Error(err)
		}
		return err
	}

	return nil
}

func writeCreateTime(ctx context.Context, recordingID data.RecordingID, now time.Time) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	path := filepath.Join(viper.GetString("record.dir"), string(recordingID))
	filename := filepath.Join(path, ".createtime")
	data := map[string]interface{}{}
	data["createTime"] = now.Unix()
	dataBytes, _ := json.Marshal(data)
	err := ioutil.WriteFile(filename, dataBytes, 0644)
	if err != nil {
		log.WithError(err).Error("write fail")
	}

	return nil
}

func readCreateTime(ctx context.Context, recordingID data.RecordingID) (time.Time, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	path := filepath.Join(viper.GetString("record.dir"), string(recordingID))
	filename := filepath.Join(path, ".createtime")
	infoFile, err := ioutil.ReadFile(filename)
	if err != nil {
		log.WithError(err).Error("read fail")
		return time.Time{}, err
	}

	var data map[string]interface{}
	json.Unmarshal(infoFile, &data)

	return time.Unix(int64(data["createTime"].(float64)), 0).UTC(), nil
}
