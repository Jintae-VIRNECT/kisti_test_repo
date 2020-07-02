package logger

import (
	"fmt"
	"os"
	"path/filepath"
	"runtime"
	"strings"

	"github.com/spf13/viper"

	"github.com/sirupsen/logrus"
	"gopkg.in/natefinch/lumberjack.v2"
)

func Init() {
	formatter := &logrus.TextFormatter{
		CallerPrettyfier: func(f *runtime.Frame) (string, string) {
			filename := filepath.Base(f.File)
			return "", fmt.Sprint(filename, ":", f.Line)
		},
		FullTimestamp: true,
		ForceColors:   true,
	}
	logrus.SetFormatter(formatter)
	logrus.SetReportCaller(true)

	if viper.GetBool("log.stdout") {
		logrus.SetOutput(os.Stdout)
	} else {
		lumberjackLogrotate := &lumberjack.Logger{
			Filename:   viper.GetString("log.filename"),
			MaxSize:    viper.GetInt("log.maxSize"),    // Max megabytes before log is rotated
			MaxBackups: viper.GetInt("log.maxBackups"), // Max number of old log files to keep
			MaxAge:     viper.GetInt("log.maxAge"),     // Max number of days to retain log files
			Compress:   false,
		}
		logrus.SetOutput(lumberjackLogrotate)
	}

	SetLevel(viper.GetString("log.level"))
}

func SetLevel(level string) {
	switch strings.ToUpper(level) {
	case "TRACE":
		logrus.SetLevel(logrus.TraceLevel)
	case "DEBUG":
		logrus.SetLevel(logrus.DebugLevel)
	case "INFO":
		logrus.SetLevel(logrus.InfoLevel)
	case "WARN":
		logrus.SetLevel(logrus.WarnLevel)
	case "ERROR":
		logrus.SetLevel(logrus.ErrorLevel)
	default:
		logrus.SetLevel(logrus.DebugLevel)
	}
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
