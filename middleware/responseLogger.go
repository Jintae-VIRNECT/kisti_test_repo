package middleware

import (
	"RM-RecordServer/data"
	"bytes"
	"encoding/json"

	"github.com/gin-gonic/gin"
	"github.com/sirupsen/logrus"
)

type bodyLogWriter struct {
	gin.ResponseWriter
	body *bytes.Buffer
}

func (w bodyLogWriter) Write(b []byte) (int, error) {
	w.body.Write(b)
	return w.ResponseWriter.Write(b)
}

func ResponseLogger() gin.HandlerFunc {
	return func(c *gin.Context) {
		blw := &bodyLogWriter{body: bytes.NewBufferString(""), ResponseWriter: c.Writer}
		c.Writer = blw
		c.Next()

		var prettyJSON bytes.Buffer
		json.Indent(&prettyJSON, blw.body.Bytes(), "", "\t")
		log := c.Request.Context().Value(data.ContextKeyLog).(*logrus.Entry)
		log.Info("Response body: " + prettyJSON.String())
	}
}
