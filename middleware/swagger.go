package middleware

import "github.com/gin-gonic/gin"

func Swagger() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Request.RequestURI = c.Request.RequestURI + "/doc.json"
		c.Next()
	}
}
