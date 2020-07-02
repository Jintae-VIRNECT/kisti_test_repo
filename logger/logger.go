package logger

import (
	"fmt"
	"runtime"
	"strings"

	"github.com/sirupsen/logrus"
)

func Init() {
	// logrus.SetOutput(os.Stdout)

	formatter := &logrus.TextFormatter{
		CallerPrettyfier: func(f *runtime.Frame) (string, string) {
			return "", fmt.Sprintf("%s:%d", formatFilePath(f.File), f.Line)
		},
		FullTimestamp: true,
	}
	logrus.SetFormatter(formatter)
	logrus.SetReportCaller(true)
	logrus.SetLevel(logrus.DebugLevel)
}

func formatFilePath(path string) string {
	arr := strings.Split(path, "/")
	return arr[len(arr)-1]
}

var (
	Info   = logrus.Info
	Infof  = logrus.Infof
	Debug  = logrus.Debug
	Debugf = logrus.Debugf
	Trace  = logrus.Trace
	Tracef = logrus.Tracef
	Error  = logrus.Error
	Errorf = logrus.Errorf
	Warn   = logrus.Warn
	Warnf  = logrus.Warnf
)
