package main

import (
	"RM-RecordServer/api"
	"RM-RecordServer/dockerclient"
	//_ "RM-RecordServer/docs"
	"RM-RecordServer/eurekaclient"
	"RM-RecordServer/logger"
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"strconv"
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
	r.POST("/media/recorder/recording", api.StartRecording)
	r.DELETE("/media/recorder/recording/:id", api.StopRecording)
	r.GET("/media/recorder/recordings", api.ListRecordings)
	r.GET("/media/recorder/files", api.ListRecordingFiles)
	r.DELETE("/media/recorder/files", api.RemoveRecordingFiles)
	r.GET("/health", func(c *gin.Context) {
		c.Writer.WriteHeader(200)
	})

	url := ginSwagger.URL("http://localhost:8083/swagger/doc.json") // The url pointing to API definition
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler, url))
	return r
}

func main() {
	readConfig()
	logger.Init()
	displayConfig()

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

	var euraka *eurekaclient.EurekaClient
	if viper.GetBool("eureka.enable") == true {
		euraka = eurekaclient.NewClient()
		euraka.Register()
	}

	// Wait for interrupt signal to gracefully shutdown the server with
	// a timeout of 5 seconds.
	quit := make(chan os.Signal)
	signal.Notify(quit, os.Interrupt)
	<-quit

	if viper.GetBool("eureka.enable") == true {
		euraka.DeRegister()
	}

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
	pflag.StringVarP(&configPath, "config", "c", "config.ini", "path to config file")
	pflag.BoolVarP(&logStdout, "stdout", "s", false, "only output to stdout")

	pflag.Parse()

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
}

func displayConfig() {
	// show all settings
	bs, _ := yaml.Marshal(viper.AllSettings())
	logger.Info("settings\n", string(bs))
}
