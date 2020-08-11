package dockerclient

import (
	"RM-RecordServer/logger"
	"context"
	"encoding/json"
	"errors"
	"net/url"
	"path/filepath"
	"strconv"
	"time"

	docker "github.com/fsouza/go-dockerclient"
	"github.com/spf13/viper"
)

type ContainerParam struct {
	RecordingID string
	Token       string
	VideoID     string
	VideoName   string
	Resolution  string
	Framerate   uint
	VideoFormat string
	LayoutURL   string
	TimeLimit   int
	SessionID   string
	UserData    interface{}
}

var (
	ErrContainerAlreadyExists = docker.ErrContainerAlreadyExists
	ErrContainerInternal      = errors.New("Container Internal Error")
)

type Container struct {
	ID          string
	RecordingID string
	EndTime     int64
}

type recordingJson struct {
	RecordingID string      `json:"recordingId"`
	SessionID   string      `json:"sessionId"`
	Filename    string      `json:"filename"`
	Framerate   uint        `json:"framerate"`
	Resolution  string      `json:"resolution"`
	UserData    interface{} `json:"userData,omitempty"`
}

func init() {
	go garbageCollector()
}

func garbageCollector() {
	period := viper.GetInt("general.garbageCollectPeriod")
	if period == 0 {
		logger.Info("disable docker containers garbageCollector")
		return
	}

	ticker := time.NewTicker(time.Duration(period) * time.Hour)
	defer ticker.Stop()
	for range ticker.C {
		go func() {
			cli, err := docker.NewClientFromEnv()
			if err != nil {
				logger.Error("NewClientFromEnv:", err)
				return
			}
			now := time.Now().UTC().Unix()
			filter := map[string][]string{
				"label": []string{"recordingId"},
			}
			cons, err := cli.ListContainers(docker.ListContainersOptions{All: true, Filters: filter})
			for _, c := range cons {
				endTime, _ := strconv.ParseInt(c.Labels["endTime"], 10, 64)
				if now > endTime+60 {
					logger.Infof("remove container which state is not running. id:%s state:%s recordId:%s createTime:%d endTime:%d",
						c.ID,
						c.State,
						c.Labels["recordingId"],
						c.Created,
						endTime)
					StopContainer(c.ID)
				}
			}
		}()
	}
}

func ListContainers() []Container {
	containers := []Container{}

	cli, err := docker.NewClientFromEnv()
	if err != nil {
		logger.Error("NewClientFromEnv:", err)
		return containers
	}

	filter := map[string][]string{
		"label":  []string{"recordingId"},
		"status": []string{"running"},
	}
	cons, err := cli.ListContainers(docker.ListContainersOptions{Filters: filter})
	for _, c := range cons {
		endTime, _ := strconv.ParseInt(c.Labels["endTime"], 10, 64)
		containers = append(containers, Container{
			ID:          c.ID,
			RecordingID: c.Labels["recordingId"],
			EndTime:     endTime,
		})
	}

	return containers
}

func DownloadDockerImage() error {
	cli, err := docker.NewClientFromEnv()
	if err != nil {
		logger.Error("NewClientFromEnv:", err)
		return ErrContainerInternal
	}

	imageName := viper.GetString("record.dockerImage")
	image, err := cli.InspectImage(imageName)
	if image != nil {
		logger.Info("already exist image:", imageName)
		return nil
	}

	logger.Info("Downloading docker image:", imageName)
	err = cli.PullImage(
		docker.PullImageOptions{Repository: imageName},
		docker.AuthConfiguration{},
	)
	if err != nil {
		logger.Error("PullImage:", err)
		return ErrContainerInternal
	}

	return nil
}

// https://OPENVIDUAPP:MY_SECRET@172.20.194.76:4443/dashboard/#/layout-best-fit/ses_R8y9uSOUn7/MY_SECRET/4443/false

func RunContainer(param ContainerParam) (string, error) {
	cli, err := docker.NewClientFromEnv()
	if err != nil {
		logger.Error("NewClientFromEnv:", err)
		return "", ErrContainerInternal
	}

	// make recording json
	filename := filepath.Join(viper.GetString("record.dirOnDocker"), param.RecordingID, param.VideoName) + "." + param.VideoFormat
	recordingJson, err := json.Marshal(
		&recordingJson{
			RecordingID: param.RecordingID,
			SessionID:   param.SessionID,
			UserData:    param.UserData,
			Filename:    filename,
			Framerate:   param.Framerate,
			Resolution:  param.Resolution,
		})
	if err != nil {
		logger.Error("userdata parsing fail:", err)
	}
	logger.Debug(string(recordingJson))

	// make url
	baseUrl, err := url.Parse(param.LayoutURL)
	params := url.Values{}
	params.Add("sessionId", param.SessionID)
	params.Add("token", param.Token)
	baseUrl.RawQuery = params.Encode() // Escape Query Parameters
	logger.Debug(baseUrl.String())

	now := time.Now().UTC().Unix()
	endTime := now + int64(param.TimeLimit*60)
	createOpt := docker.CreateContainerOptions{}
	createOpt.Name = param.RecordingID
	createOpt.Config = &docker.Config{
		Image: viper.GetString("record.dockerImage"),
		Env: []string{
			"URL=" + param.LayoutURL + "?" + baseUrl.String(),
			"ONLY_VIDEO=" + "false",
			"RESOLUTION=" + param.Resolution,
			"FRAMERATE=" + strconv.Itoa(int(param.Framerate)),
			"VIDEO_ID=" + param.VideoID,
			"VIDEO_NAME=" + param.VideoName,
			"VIDEO_FORMAT=" + param.VideoFormat,
			"RECORDING_JSON=" + string(recordingJson),
		},
		Labels: map[string]string{
			"recordingId": param.RecordingID,
			"endTime":     strconv.FormatInt(endTime, 10),
		},
	}

	// dev 서버에서는 build 후에 prune을 하고 있어 recording image가 삭제되어 있을 수 있다.
	DownloadDockerImage()

	createOpt.HostConfig = &docker.HostConfig{
		Binds: []string{viper.GetString("record.dirOnHost") + ":" + viper.GetString("record.dirOnDocker")},
	}
	container, err := cli.CreateContainer(createOpt)
	if err != nil {
		logger.Error("CreateContainer:", err)
		if err == docker.ErrContainerAlreadyExists {
			return "", ErrContainerAlreadyExists
		}
		return "", ErrContainerInternal
	}

	err = cli.StartContainer(container.ID, &docker.HostConfig{})
	if err != nil {
		logger.Error("StartContainer:", err)
		err = cli.RemoveContainer(docker.RemoveContainerOptions{ID: container.ID})
		if err != nil {
			logger.Error("RemoveContainer:", err)
		}
		return "", ErrContainerInternal
	}
	logger.Info("start container:", container.ID, " sessionId:", param.VideoID)

	return container.ID, nil
}

func StopContainer(containerID string) error {
	go stopAndRemoveContainer(containerID)
	return nil
}

func stopAndRemoveContainer(containerID string) {
	cli, err := docker.NewClientFromEnv()
	if err != nil {
		logger.Error("NewClientFromEnv:", err)
		return
	}
	var timeout = time.Duration(5) * time.Second
	ctx, cancel := context.WithCancel(context.Background())
	time.AfterFunc(timeout, func() {
		cancel()
	})

	go func(containerID string, ctx context.Context) {
		defer func() {
			logger.Info("stop container:", containerID)
		}()

		cmd := docker.CreateExecOptions{
			Cmd:          []string{"bash", "-c", "echo 'q' > stop"},
			Container:    containerID,
			AttachStdout: true,
			AttachStderr: true,
		}
		if exec, err := cli.CreateExec(cmd); err == nil {
			err = cli.StartExec(exec.ID, docker.StartExecOptions{Context: ctx})
			if err != nil {
				logger.Error("StartExec:", err)
			}

			rc, err := cli.WaitContainerWithContext(containerID, ctx)
			if err != nil {
				logger.Error("WaitContainer:", err)
			}
			logger.Debugf("WaitContainer: %d containerId:%s", rc, containerID)
		} else {
			logger.Error("CreateExec:", err)
		}

		err = cli.RemoveContainer(docker.RemoveContainerOptions{ID: containerID, Force: true})
		if err != nil {
			logger.Error("RemoveContainer:", err)
		}
	}(containerID, ctx)
}
