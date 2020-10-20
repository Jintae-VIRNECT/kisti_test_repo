package util

import (
	"RM-RecordServer/logger"
	"io/ioutil"
	"math/rand"
	"net"
	"os"
	"path"
	"regexp"
	"strings"
	"syscall"
	"time"
)

const (
	B  = 1
	KB = 1024 * B
	MB = 1024 * KB
	GB = 1024 * MB
)

type DiskStatus struct {
	All  uint64 `json:"all"`
	Used uint64 `json:"used"`
	Free uint64 `json:"free"`
}

const charset = "abcdefghijklmnopqrstuvwxyz" +
	"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

var seededRand *rand.Rand = rand.New(
	rand.NewSource(time.Now().UnixNano()))

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

func DiskUsage(path string) (DiskStatus, error) {
	disk := DiskStatus{}
	fs := syscall.Statfs_t{}
	err := syscall.Statfs(path, &fs)
	if err != nil {
		return disk, err
	}
	disk.All = fs.Blocks * uint64(fs.Bsize)
	disk.Free = fs.Bfree * uint64(fs.Bsize)
	disk.Used = disk.All - disk.Free
	return disk, nil
}

func RemoveContents(dirname string) (int, error) {
	dir, err := ioutil.ReadDir(dirname)
	if err != nil {
		return 0, err
	}
	var count int
	for _, d := range dir {
		err = os.RemoveAll(path.Join([]string{dirname, d.Name()}...))
		if err != nil {
			logger.Warn("remove:", err)
			continue
		}
		count++
	}
	return count, nil
}

func RandomString(length int) string {
	b := make([]byte, length)
	for i := range b {
		b[i] = charset[seededRand.Intn(len(charset))]
	}
	return string(b)
}

var matchFirstCap = regexp.MustCompile("(.)([A-Z][a-z]+)")
var matchAllCap = regexp.MustCompile("([a-z0-9])([A-Z])")

func ToSnakeCase(str string) string {
	snake := matchFirstCap.ReplaceAllString(str, "${1}_${2}")
	snake = matchAllCap.ReplaceAllString(snake, "${1}_${2}")
	return strings.ToLower(snake)
}
