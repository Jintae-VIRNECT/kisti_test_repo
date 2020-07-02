package main

import (
	"RM-RecordServer/api"
	"RM-RecordServer/docker"
	_ "RM-RecordServer/docs"
	"RM-RecordServer/logger"

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
	logger.Init()
	docker.DownloadDockerImage()

	r := SetupRouter()
	url := ginSwagger.URL("http://localhost:8080/swagger/doc.json") // The url pointing to API definition
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler, url))

	r.Run()
}
