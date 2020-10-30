package main

import (
	"RM-RecordServer/api"
	"RM-RecordServer/data"
	"RM-RecordServer/dockerclient"
	_ "RM-RecordServer/docs"
	"RM-RecordServer/eurekaclient"
	"RM-RecordServer/logger"
	"RM-RecordServer/recorder"
	"RM-RecordServer/storage"
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
	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	ginSwagger "github.com/swaggo/gin-swagger"
	swaggerFiles "github.com/swaggo/gin-swagger/swaggerFiles"
)

// @title VIRNECT Remote Record Server API Document
// @version 1.0
// @description This is Remote Record Server API Document
func main() {

	// read configuration from config.ini
	readConfig()

	// initialize global logger
	logger.Init()

	// display configuration
	displayConfig()

	// initialize recorder
	recorder.Init()

	// initialize docker client
	dockerclient.Init()

	// initialize storage
	storageClient := storage.GetClient()
	storageClient.Init()

	// setup gin router
	router := setupRouter()

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

func setupRouter() *gin.Engine {
	if viper.GetBool("general.devMode") == false {
		gin.SetMode(gin.ReleaseMode)
	}

	r := gin.New()
	r.Use(loggerMiddleware())
	r.Use(requestLoggerMiddleware())
	r.Use(CustomRecovery())

	recorder := r.Group("/remote/recorder")
	{
		recording := recorder.Group("/workspaces/:workspaceId/users/:userId/recordings")
		{
			recording.POST("", api.StartRecording)
			recording.DELETE("", api.StopRecordingBySessionID)
			recording.DELETE(":id", api.StopRecording)
			recording.GET("", api.ListRecordings)
		}
		file := recorder.Group("/workspaces/:workspaceId/users/:userId/files")
		{
			file.DELETE("", api.RemoveRecordingFileAll)
			file.DELETE(":id", api.RemoveRecordingFile)
			file.GET("", api.ListRecordingFiles)
			file.GET(":id/url", api.GetRecordingFileDownloadUrl)
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

func requestLoggerMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		if strings.Contains(c.Request.RequestURI, "swagger") {
			c.Next()
			return
		}
		log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)

		var buf bytes.Buffer
		tee := io.TeeReader(c.Request.Body, &buf)
		body, _ := ioutil.ReadAll(tee)
		c.Request.Body = ioutil.NopCloser(&buf)
		logbuf := fmt.Sprintf("Request method:%s path:%s user-agent:%s", c.Request.Method, c.Request.URL.Path, c.GetHeader("User-Agent"))
		if len(body) > 0 {
			logbuf = fmt.Sprintf("%s\nbody:%s", logbuf, string(body))
		}
		log.Info(logbuf)
		c.Next()
	}
}

func loggerMiddleware() gin.HandlerFunc {
	const headerXRequestID = "X-Request-ID"
	logger := logger.NewLogger()
	return func(c *gin.Context) {
		reqID := c.GetHeader(headerXRequestID)
		if reqID == "" {
			reqID = uuid.New().String()
			c.Header(headerXRequestID, reqID)
		}

		logEntry := logrus.NewEntry(logger)
		logEntry = logEntry.WithField("request_id", reqID)
		c.Request = c.Request.WithContext(context.WithValue(c.Request.Context(), data.ContextKeyLog, logEntry))
		c.Next()
	}
}

func swaggerMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Request.RequestURI = c.Request.RequestURI + "/doc.json"
		c.Next()
	}
}
