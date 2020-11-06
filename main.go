package main

import (
	"RM-RecordServer/api"
	"RM-RecordServer/dockerclient"
	_ "RM-RecordServer/docs"
	"RM-RecordServer/eurekaclient"
	"RM-RecordServer/logger"
	"RM-RecordServer/middleware"
	"RM-RecordServer/recorder"
	"RM-RecordServer/storage"
	"context"
	"crypto/tls"
	"net/http"
	"os"
	"os/signal"
	"strconv"
	"time"

	"github.com/arl/statsviz"
	"github.com/gin-gonic/gin"
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

	// initialize storage
	storage.Init()

	// initialize recorder
	recorder.Init()

	// initialize docker client
	dockerclient.Init()

	// setup gin router
	router := setupRouter()

	srv := &http.Server{
		Addr:    ":" + strconv.Itoa(viper.GetInt("general.port")),
		Handler: router,
	}

	logger := logger.NewLogger()

	go func() {
		logger.Info("Server Started: listen:", viper.GetInt("general.port"))
		var err error
		if viper.GetBool("general.useSSL") {
			srv.TLSConfig = &tls.Config{
				MinVersion: tls.VersionTLS12,
			}
			certFile := viper.GetString("general.cert")
			keyFile := viper.GetString("general.key")
			err = srv.ListenAndServeTLS(certFile, keyFile)
		} else {
			err = srv.ListenAndServe()
		}
		if err != nil {
			logger.WithError(err).Error("listen fail")
		}
	}()

	if viper.GetBool("general.devMode") {
		go func() {
			statsviz.RegisterDefault()
			err := http.ListenAndServe(":6060", nil)
			if err != nil {
				logger.WithError(err).Error("listen fail")
			}
		}()
	}

	euraka := eurekaclient.GetClient()
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
		logger.WithError(err).Error("Server Shutdown")
	}
	logger.Info("Record Server stopped")
}

func setupRouter() *gin.Engine {
	if viper.GetBool("general.devMode") == false {
		gin.SetMode(gin.ReleaseMode)
	}

	r := gin.New()
	r.Use(middleware.Logger())
	r.Use(middleware.RequestLogger())
	r.Use(middleware.ResponseLogger())
	r.Use(middleware.CustomRecovery())

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
	r.GET("/v2/api-docs/*any", middleware.Swagger(), ginSwagger.WrapHandler(swaggerFiles.Handler))
	return r
}
