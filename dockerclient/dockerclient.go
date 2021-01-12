package dockerclient

import (
	"RM-RecordServer/data"
	"RM-RecordServer/logger"
	"bufio"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"path/filepath"
	"strconv"
	"strings"
	"time"

	docker "github.com/fsouza/go-dockerclient"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
)

type ContainerParam struct {
	RecordingID data.RecordingID
	Token       string
	VideoID     string
	Filename    string
	Resolution  string
	Framerate   int
	LayoutURL   string
	TimeLimit   int
	SessionID   data.SessionID
	WorkspaceID data.WorkspaceID
	UserID      string
	MetaData    interface{}
}

var (
	ErrContainerAlreadyExists = errors.New("Container Already Exists Error")
	ErrContainerNoSuchImage   = errors.New("Container No Such Image Error")
	ErrContainerTimeout       = errors.New("Container Timeout")
	ErrContainerInternal      = errors.New("Container Internal Error")
)

type ContainerLabel struct {
	ID          string
	RecordingID string
	SessionID   string
	WorkspaceID string
	UserID      string
	EndTime     int64
	TimeLimit   int
}

type recordingJson struct {
	RecordingID data.RecordingID `json:"recordingId"`
	WorkspaceID data.WorkspaceID `json:"workspaceId"`
	UserID      string           `json:"userId"`
	SessionID   data.SessionID   `json:"sessionId"`
	Filename    string           `json:"filename"`
	Framerate   int              `json:"framerate"`
	Resolution  string           `json:"resolution"`
	TimeLimit   int              `json:"timeLimit"`
	MetaData    interface{}      `json:"metaData,omitempty"`
}

func Init() {
	log := logger.NewLogger()
	logEntry := logrus.NewEntry(log)
	ctx := context.WithValue(context.Background(), data.ContextKeyLog, logEntry)

	if err := DownloadDockerImage(ctx); err != nil {
		e := fmt.Errorf("not found docker image: %s", viper.GetString("record.dockerImage"))
		panic(e)
	}
	go garbageCollector(ctx)
	go monitoringRecordingImage(ctx)
}

func monitoringRecordingImage(ctx context.Context) {
	period := time.Duration(10) * time.Second
	ticker := time.NewTicker(period)
	defer ticker.Stop()
	for range ticker.C {
		DownloadDockerImage(ctx)
	}
}

func garbageCollector(ctx context.Context) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	period := viper.GetInt("general.garbageCollectPeriod")
	if period == 0 {
		log.Info("disable docker containers garbageCollector")
		return
	}

	ticker := time.NewTicker(time.Duration(period) * time.Hour)
	defer ticker.Stop()
	for range ticker.C {
		go func() {
			cli, err := docker.NewClientFromEnv()
			if err != nil {
				log.Error("NewClientFromEnv:", err)
				return
			}
			now := time.Now().Unix()
			filter := map[string][]string{
				"ancestor": []string{viper.GetString("record.dockerImage")},
			}
			cons, err := cli.ListContainers(docker.ListContainersOptions{All: true, Filters: filter})
			for _, c := range cons {
				endTime, _ := strconv.ParseInt(c.Labels["endTime"], 10, 64)
				if now > endTime+60 {
					log.Infof("remove container which state is not running. id:%s state:%s recordingId:%s sessionId:%s workspaceId:%s userId:%s createTime:%d endTime:%d",
						c.ID,
						c.State,
						c.Labels["recordingId"],
						c.Labels["sessionId"],
						c.Labels["workspaceId"],
						c.Labels["userId"],
						c.Created,
						endTime)
					StopContainer(ctx, c.ID)
				}
			}
		}()
	}
}

func ListContainers(ctx context.Context) []ContainerLabel {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	containers := []ContainerLabel{}

	cli, err := docker.NewClientFromEnv()
	if err != nil {
		log.Error("NewClientFromEnv:", err)
		return containers
	}

	filter := map[string][]string{
		"label":  []string{"recordingId"},
		"status": []string{"running"},
	}
	cons, err := cli.ListContainers(docker.ListContainersOptions{Filters: filter})
	for _, c := range cons {
		endTime, _ := strconv.ParseInt(c.Labels["endTime"], 10, 64)
		timeLimit, _ := strconv.ParseInt(c.Labels["timeLimit"], 10, 64)
		containers = append(containers, ContainerLabel{
			ID:          c.ID,
			RecordingID: c.Labels["recordingId"],
			SessionID:   c.Labels["sessionId"],
			WorkspaceID: c.Labels["workspaceId"],
			UserID:      c.Labels["userId"],
			TimeLimit:   int(timeLimit),
			EndTime:     endTime,
		})
	}

	return containers
}

func DownloadDockerImage(ctx context.Context) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	cli, err := docker.NewClientFromEnv()
	if err != nil {
		log.Error("NewClientFromEnv:", err)
		return ErrContainerInternal
	}

	imageName := viper.GetString("record.dockerImage")
	image, err := cli.InspectImage(imageName)
	if image != nil {
		return nil
	}

	logger.Info("Downloading docker image:", imageName)
	err = cli.PullImage(
		docker.PullImageOptions{Repository: imageName},
		docker.AuthConfiguration{},
	)
	if err != nil {
		log.Error("PullImage:", err)
		return ErrContainerInternal
	}
	logger.Info("Complete Downloading docker image:", imageName)

	return nil
}

func RunContainer(ctx context.Context, param ContainerParam) (string, error) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	cli, err := docker.NewClientFromEnv()
	if err != nil {
		log.Error("NewClientFromEnv:", err)
		return "", ErrContainerInternal
	}

	token := strings.Split(param.Filename, ".")
	videoName := token[0]
	videoFormat := ""
	if len(token) == 1 {
		videoFormat = viper.GetString("record.defaultVideoFormat")
	} else {
		videoFormat = token[1]
	}
	codecParam := viper.GetString(strings.Join([]string{"format", videoFormat}, "."))
	log.Debug("codec parameter:", codecParam)

	// make recording json
	filename := filepath.Join(viper.GetString("record.dirOnDocker"), param.RecordingID.String(), videoName) + "." + videoFormat
	recordingJson, err := json.Marshal(
		&recordingJson{
			RecordingID: param.RecordingID,
			WorkspaceID: param.WorkspaceID,
			UserID:      param.UserID,
			SessionID:   param.SessionID,
			MetaData:    param.MetaData,
			Filename:    filename,
			Framerate:   param.Framerate,
			Resolution:  param.Resolution,
			TimeLimit:   param.TimeLimit,
		})
	if err != nil {
		log.Error("metaData parsing fail:", err)
	}
	log.Debug(string(recordingJson))

	url := param.LayoutURL + "?sessionId=" + param.SessionID.String() + "&token=" + param.Token
	log.Info("url:", url)

	endTime := time.Now().Unix() + int64(param.TimeLimit)
	createOpt := docker.CreateContainerOptions{}
	createOpt.Name = param.RecordingID.String()
	createOpt.Config = &docker.Config{
		Image: viper.GetString("record.dockerImage"),
		Env: []string{
			"URL=" + url,
			"ONLY_VIDEO=" + "false",
			"RESOLUTION=" + param.Resolution,
			"FRAMERATE=" + strconv.Itoa(int(param.Framerate)),
			"VIDEO_ID=" + param.VideoID,
			"VIDEO_NAME=" + videoName,
			"VIDEO_FORMAT=" + videoFormat,
			"CODEC_PARAMETER=" + codecParam,
			"RECORDING_JSON=" + string(recordingJson),
		},
		Labels: map[string]string{
			"recordingId": param.RecordingID.String(),
			"sessionId":   param.SessionID.String(),
			"workspaceId": param.WorkspaceID.String(),
			"userId":      param.UserID,
			"timeLimit":   strconv.FormatInt(int64(param.TimeLimit), 10),
			"endTime":     strconv.FormatInt(endTime, 10),
		},
	}

	createOpt.HostConfig = &docker.HostConfig{
		Binds: []string{viper.GetString("record.dirOnHost") + ":" + viper.GetString("record.dirOnDocker")},
	}
	container, err := cli.CreateContainer(createOpt)
	if err != nil {
		log.Error("CreateContainer:", err)
		if err == docker.ErrContainerAlreadyExists {
			return "", ErrContainerAlreadyExists
		} else if err == docker.ErrNoSuchImage {
			return "", ErrContainerNoSuchImage
		}
		return "", ErrContainerInternal
	}

	timeout := 5 * time.Second
	timoutCtx, cancel := context.WithTimeout(ctx, timeout)
	defer cancel()

	err = cli.StartContainerWithContext(container.ID, &docker.HostConfig{}, timoutCtx)
	if err != nil {
		log.WithError(err).Error("StartContainer")
		if e := cli.RemoveContainer(docker.RemoveContainerOptions{ID: container.ID, Force: true}); e != nil {
			log.WithError(e).Warn("RemoveContainer is fail")
		}
		return "", err
	}
	log.Info("start container:", container.ID, " sessionId:", param.VideoID)

	err = waitStartRecording(timoutCtx, container.ID)
	if err != nil {
		log.WithError(err).Error("waitStartRecording")
		if e := cli.RemoveContainer(docker.RemoveContainerOptions{ID: container.ID, Force: true}); e != nil {
			log.WithError(e).Warn("RemoveContainer is fail")
		}
		return "", err
	}

	return container.ID, nil
}

func waitStartRecording(ctx context.Context, containerID string) error {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	cli, err := docker.NewClientFromEnv()
	if err != nil {
		log.Error("NewClientFromEnv:", err)
		return ErrContainerInternal
	}

	r, w := io.Pipe()
	closeWaiter, err := cli.AttachToContainerNonBlocking(
		docker.AttachToContainerOptions{
			Container:    containerID,
			OutputStream: w,
			ErrorStream:  w,
			Logs:         false,
			Stdout:       true,
			Stderr:       true,
			Stream:       true,
		},
	)
	if err != nil {
		log.WithError(err).Warn("fail AttachToContainerNonBlocking")
		return err
	}

	defer func() {
		err = closeWaiter.Close()
		if err != nil {
			log.WithError(err).Error("Could not close container log")
		}
		err = closeWaiter.Wait()
		if err != nil {
			log.WithError(err).Error("Could not wait for container log to close")
		}
		log.Debug("dettach container")
	}()

	ch := make(chan error)
	defer close(ch)

	go func() {
		reader := bufio.NewReader(r)
		for {
			line, err := reader.ReadString('\n')
			if err != nil {
				switch err {
				case io.EOF:
					log.Info("Container has closed its IO channel")
				default:
					log.WithError(err).Error("Error reading container output")
				}
				ch <- err
				break
			}
			log.Debug(line)

			if strings.Contains(line, "Press [q] to stop") {
				ch <- nil
				break
			}
		}
	}()

	for {
		select {
		case err := <-ch:
			return err
		case <-ctx.Done():
			return ErrContainerTimeout
		}
	}
}

func StopContainer(ctx context.Context, containerID string) {
	stopAndRemoveContainer(ctx, containerID)
}

func stopAndRemoveContainer(ctx context.Context, containerID string) {
	log := ctx.Value(data.ContextKeyLog).(*logrus.Entry)

	cli, err := docker.NewClientFromEnv()
	if err != nil {
		log.Error("NewClientFromEnv:", err)
		return
	}
	var timeout = time.Duration(5) * time.Second
	stopCtx, cancel := context.WithCancel(ctx)
	time.AfterFunc(timeout, func() {
		cancel()
	})

	cmd := docker.CreateExecOptions{
		Cmd:          []string{"bash", "-c", "echo 'q' > stop"},
		Container:    containerID,
		AttachStdout: true,
		AttachStderr: true,
	}
	if exec, err := cli.CreateExec(cmd); err == nil {
		err = cli.StartExec(exec.ID, docker.StartExecOptions{Context: stopCtx})
		if err != nil {
			log.Error("StartExec:", err)
		}

		rc, err := cli.WaitContainerWithContext(containerID, stopCtx)
		if err != nil {
			log.Error("WaitContainer:", err)
		}
		log.Debugf("WaitContainer: %d containerId:%s", rc, containerID)
	} else {
		log.Error("CreateExec:", err)
	}

	log.Info("stop container:", containerID)

	err = cli.RemoveContainer(docker.RemoveContainerOptions{ID: containerID, Force: true})
	if err != nil {
		log.Error("RemoveContainer:", err)
	}
}
