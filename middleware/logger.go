package middleware

import (
	"RM-RecordServer/data"
	"RM-RecordServer/logger"
	"context"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/sirupsen/logrus"
)

func Logger() gin.HandlerFunc {
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
