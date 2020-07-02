package dockerclient

import (
	"RM-RecordServer/logger"
	"context"
	"errors"
	"strconv"
	"time"

	docker "github.com/fsouza/go-dockerclient"
	"github.com/spf13/viper"
)

type ContainerParam struct {
	VideoID     string
	VideoName   string
	Resolution  string
	Framerate   int
	VideoFormat string
	LayoutURL   string
}

var (
	ErrContainerAlreadyExists = docker.ErrContainerAlreadyExists
	ErrContainerInternal      = errors.New("Container Internal Error")
)

func DownloadDockerImage() error {
	cli, err := docker.NewClientFromEnv()
	if err != nil {
		logger.Error("NewClientFromEnv:", err)
		return ErrContainerInternal
	}

	err = cli.PullImage(
		docker.PullImageOptions{Repository: viper.GetString("record.dockerImage")},
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
	createOpt := docker.CreateContainerOptions{}
	createOpt.Name = param.VideoID
	createOpt.Config = &docker.Config{
		Image: viper.GetString("record.dockerImage"),
		Env: []string{
			"URL=" + param.LayoutURL + "/" + param.VideoID + "/MY_SECRET/4443/false",
			"ONLY_VIDEO=" + "false",
			"RESOLUTION=" + convertResolution(param.Resolution),
			"FRAMERATE=" + strconv.Itoa(param.Framerate),
			"VIDEO_ID=" + param.VideoID,
			"VIDEO_NAME=" + param.VideoName,
			"VIDEO_FORMAT=" + param.VideoFormat,
			"RECORDING_JSON=" + "{}",
		}}

	createOpt.HostConfig = &docker.HostConfig{
		Binds: []string{viper.GetString("record.dir") + ":/recordings"},
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
		exec, err := cli.CreateExec(cmd)
		if err != nil {
			logger.Error("CreateExec:", err)
		}
		err = cli.StartExec(exec.ID, docker.StartExecOptions{Context: ctx})
		if err != nil {
			logger.Error("StartExec:", err)
		}

		rc, err := cli.WaitContainerWithContext(containerID, ctx)
		if err != nil {
			logger.Error("WaitContainer:", err)
		}
		logger.Debugf("WaitContainer: %d containerId:%s", rc, containerID)

		err = cli.RemoveContainer(docker.RemoveContainerOptions{ID: containerID, Force: true})
		if err != nil {
			logger.Error("RemoveContainer:", err)
		}
	}(containerID, ctx)
}

func convertResolution(res string) string {
	switch res {
	case "480p":
		return "640x480"
	case "720p":
		return "1280x720"
	case "1080p":
		return "1920x1080"
	default:
		return "1280x720"
	}
}
