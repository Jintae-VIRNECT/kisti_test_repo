package logger

import (
	"fmt"
	"os"
	"path/filepath"
	"runtime"

	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"
	"gopkg.in/natefinch/lumberjack.v2"
)

var defaultLogger *logrus.Logger

type logFunc func(...interface{})
type logfFunc func(string, ...interface{})

var (
	Info   logFunc
	Debug  logFunc
	Trace  logFunc
	Error  logFunc
	Warn   logFunc
	Infof  logfFunc
	Debugf logfFunc
	Tracef logfFunc
	Errorf logfFunc
	Warnf  logfFunc
)

func Init() {
	defaultLogger = NewLogger()

	Info = defaultLogger.Info
	Debug = defaultLogger.Debug
	Trace = defaultLogger.Trace
	Error = defaultLogger.Error
	Warn = defaultLogger.Warn
	Infof = defaultLogger.Infof
	Debugf = defaultLogger.Debugf
	Tracef = defaultLogger.Tracef
	Errorf = defaultLogger.Errorf
	Warnf = defaultLogger.Warnf
}

func NewLogger() *logrus.Logger {
	l := logrus.New()
	initLogger(l)
	return l
}

func initLogger(l *logrus.Logger) {
	formatter := &logrus.TextFormatter{
		CallerPrettyfier: func(f *runtime.Frame) (string, string) {
			filename := filepath.Base(f.File)
			return "", fmt.Sprint(" ", filename, ":", f.Line)
		},
		TimestampFormat: "20060102-150405.000000",
		FullTimestamp:   true,
		ForceColors:     true,
	}
	l.SetFormatter(formatter)
	l.SetReportCaller(true)

	if viper.GetBool("log.stdout") {
		l.SetOutput(os.Stdout)
	} else {
		lumberjackLogrotate := &lumberjack.Logger{
			Filename:   viper.GetString("log.filename"),
			MaxSize:    viper.GetInt("log.maxSize"),    // Max megabytes before log is rotated
			MaxBackups: viper.GetInt("log.maxBackups"), // Max number of old log files to keep
			MaxAge:     viper.GetInt("log.maxAge"),     // Max number of days to retain log files
			Compress:   false,
		}
		l.SetOutput(lumberjackLogrotate)
	}

	level, _ := logrus.ParseLevel(viper.GetString("log.level"))
	l.SetLevel(level)
}
