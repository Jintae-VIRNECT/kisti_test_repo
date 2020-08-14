package main

import (
	"RM-RecordServer/api"
	"RM-RecordServer/dockerclient"
	_ "RM-RecordServer/docs"
	"RM-RecordServer/eurekaclient"
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"bytes"
	"context"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/spf13/pflag"
	"github.com/spf13/viper"
	ginSwagger "github.com/swaggo/gin-swagger"
	swaggerFiles "github.com/swaggo/gin-swagger/swaggerFiles"
	"gopkg.in/yaml.v2"
)

func SetupRouter() *gin.Engine {
	if viper.GetBool("general.devMode") == false {
		gin.SetMode(gin.ReleaseMode)
	}

	r := gin.New()
	r.Use(requestLoggerMiddleware())
	r.Use(CustomRecovery())

	recorder := r.Group("/remote/recorder")
	{
		recording := recorder.Group("/recording")
		{
			recording.POST("", api.StartRecording)
			recording.DELETE(":id", api.StopRecording)
			recording.GET("", api.ListRecordings)
		}
		file := recorder.Group("/file")
		{
			file.DELETE("", api.RemoveRecordingFileAll)
			file.DELETE(":id", api.RemoveRecordingFile)
			file.GET("", api.ListRecordingFiles)
			file.GET("download/:id", api.DownloadRecordingFile)
		}
	}

	r.GET("/health", func(c *gin.Context) {
		c.Writer.WriteHeader(200)
	})

	url := ginSwagger.URL("http://localhost:8083/swagger/doc.json")
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler, url))
	r.GET("/v2/api-docs/*any", swaggerMiddleware(), ginSwagger.WrapHandler(swaggerFiles.Handler))
	return r
}

// @title VIRNECT Remote Record Server API Document
// @version 1.0
// @description This is Remote Record Server API Document
func main() {
	readConfig()

	logger.Init()

	displayConfig()

	recorder.Init()

	restoreRecordingFromContainer()

	err := dockerclient.DownloadDockerImage()
	if err != nil {
		panic(err)
	}

	router := SetupRouter()

	srv := &http.Server{
		Addr:    ":" + strconv.Itoa(viper.GetInt("general.port")),
		Handler: router,
	}

	go func() {
		logger.Info("Server Started: listen:", viper.GetInt("general.port"))
		if err := srv.ListenAndServe(); err != nil {
			logger.Errorf("listen: %s", err)
		}
	}()

	euraka := eurekaclient.NewClient()
	euraka.Run()

	// Wait for interrupt signal to gracefully shutdown the server with
	// a timeout of 5 seconds.
	quit := make(chan os.Signal)
	signal.Notify(quit, os.Interrupt)
	<-quit

	euraka.Stop()

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err := srv.Shutdown(ctx); err != nil {
		logger.Error("Server Shutdown:", err)
	}
	logger.Info("Record Server stopped")
}

func readConfig() {
	var configPath string
	var logStdout bool
	var version bool
	pflag.BoolVarP(&version, "version", "v", false, "show version")
	pflag.StringVarP(&configPath, "config", "c", "config.ini", "path to config file")
	pflag.BoolVarP(&logStdout, "stdout", "s", false, "only output to stdout")

	pflag.Parse()

	if version {
		fmt.Printf("version: %s-%s\n", Version, Build)
		os.Exit(0)
	}

	viper.SetConfigType("toml")
	viper.SetConfigName(configPath)
	viper.AddConfigPath(".")
	err := viper.ReadInConfig() // Find and read the config file
	if err != nil {             // Handle errors reading the config file
		panic(fmt.Errorf("Fatal error config file: %s\n", err))
	}

	if logStdout {
		viper.Set("log.stdout", true)
	}

	var recDir string
	if _, err := os.Stat("/.dockerenv"); err == nil {
		logger.Info("running in docker container.")
		recDir = viper.GetString("record.dirOnDocker")
	} else {
		recDir = viper.GetString("record.dirOnHost")
	}
	viper.Set("record.dir", recDir)
	logger.Info("record Dir:", recDir)
}

func displayConfig() {
	// show all settings
	bs, _ := yaml.Marshal(viper.AllSettings())
	logger.Info("settings\n", string(bs))
}

func requestLoggerMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		if strings.Contains(c.Request.RequestURI, "swagger") {
			c.Next()
			return
		}
		var buf bytes.Buffer
		tee := io.TeeReader(c.Request.Body, &buf)
		body, _ := ioutil.ReadAll(tee)
		c.Request.Body = ioutil.NopCloser(&buf)
		logbuf := fmt.Sprintf("Request method:%s path:%s user-agent:%s", c.Request.Method, c.Request.URL.Path, c.GetHeader("User-Agent"))
		if len(body) > 0 {
			logbuf = fmt.Sprintf("%s\nbody:%s", logbuf, string(body))
		}
		logger.Info(logbuf)
		c.Next()
	}
}

func swaggerMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Request.RequestURI = c.Request.RequestURI + "/doc.json"
		c.Next()
	}
}

func restoreRecordingFromContainer() {
	logger.Info("Start: Restore Recording From Container")
	constainers := dockerclient.ListContainers()
	now := time.Now().UTC().Unix()

	for _, container := range constainers {
		recordingTimeLimit := container.EndTime - now
		recorder.RestoreRecording(container.RecordingID, container.ID, recordingTimeLimit)
	}
	logger.Info("End: Restore Recording From Container")
}
