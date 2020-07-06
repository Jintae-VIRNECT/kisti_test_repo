package util

import (
	"RM-RecordServer/logger"
	"net"
	"os"
)

func GetHostName() string {
	hostname, err := os.Hostname()
	if err != nil {
		logger.Warn("GetHostName:", err)
		return "localhost"
	}
	return hostname
}

func GetLocalIP() string {
	conn, err := net.Dial("udp", "8.8.8.8:80")
	if err != nil {
		logger.Warn(err)
		return "127.0.0.1"
	}
	defer conn.Close()
	return conn.LocalAddr().(*net.UDPAddr).IP.String()
}
