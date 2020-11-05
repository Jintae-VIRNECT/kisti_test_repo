package middleware

import (
	"RM-RecordServer/data"
	"bytes"
	"fmt"
	"io"
	"io/ioutil"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

func RequestLogger() gin.HandlerFunc {
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
