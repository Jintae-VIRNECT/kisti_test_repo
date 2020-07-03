package main

import (
	"RM-RecordServer/api"
	"RM-RecordServer/dockerclient"
	//_ "RM-RecordServer/docs"
	"RM-RecordServer/logger"
	"fmt"
	"strconv"

	"github.com/spf13/pflag"
	"github.com/spf13/viper"
	"gopkg.in/yaml.v2"

	"github.com/gin-gonic/gin"
	ginSwagger "github.com/swaggo/gin-swagger"
	swaggerFiles "github.com/swaggo/gin-swagger/swaggerFiles"
)

func SetupRouter() *gin.Engine {
	r := gin.New()
	r.POST("/recording", api.StartRecording)
	r.DELETE("/recording/:id", api.StopRecording)
	r.GET("/recordings", api.ListRecordings)
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

	r := SetupRouter()
	url := ginSwagger.URL("http://localhost:8080/swagger/doc.json") // The url pointing to API definition
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler, url))

	r.Run(":" + strconv.Itoa(viper.GetInt("general.port")))
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
