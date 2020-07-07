package eurekaclient

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/util"
	"bytes"
	"encoding/json"
	"io/ioutil"
	"net"
	"net/http"
	"strconv"
	"time"

	"github.com/hudl/fargo"
	"github.com/spf13/viper"
)

type EurekaClient struct {
	instance  *Instance
	serverURL string
	ticker    *time.Ticker
	done      chan bool
}

func NewClient() *EurekaClient {
	port := viper.GetInt("general.port")
	appName := viper.GetString("eureka.app")
	baseURL := "http://" + util.GetLocalIP() + ":" + strconv.Itoa(port) + "/"
	c := &EurekaClient{
		serverURL: viper.GetString("eureka.serverURL") + "/apps/" + viper.GetString("eureka.app"),
	}
	c.instance = &Instance{
		InstanceId:        util.GetHostName() + ":" + viper.GetString("eureka.app") + ":" + strconv.Itoa(port),
		HostName:          util.GetLocalIP(),
		App:               appName,
		IPAddr:            util.GetLocalIP(),
		Status:            UP,
		Overriddenstatus:  UNKNOWN,
		Port:              port,
		PortEnabled:       true,
		SecurePort:        443,
		SecurePortEnabled: false,
		CountryId:         1,
		DataCenterInfo: DataCenterInfo{
			Name: fargo.MyOwn,
		},
		LeaseInfo: LeaseInfo{
			RenewalIntervalInSecs: 30,
			DurationInSecs:        90,
		},
		HomePageUrl:      baseURL,
		StatusPageUrl:    baseURL + "actuator/info",
		HealthCheckUrl:   baseURL + "actuator/health",
		VipAddress:       appName,
		SecureVipAddress: appName,
		Metadata:         InstanceMetadata{parsed: map[string]interface{}{}},
	}
	c.instance.Metadata.parsed["management.port"] = strconv.Itoa(port)
	return c
}

func (c *EurekaClient) Register() {
	body, _ := json.Marshal(RegisterInstanceJson{c.instance})
	logger.Info(c.serverURL, " : ", string(body))
	req, _ := http.NewRequest(http.MethodPost, c.serverURL, bytes.NewReader(body))

	resp, rc, err := c.sendHttpRequest(req)
	if err != nil {
		logger.Error("Eureka Registration. error:", err)
		return
	}

	if rc != http.StatusNoContent {
		logger.Errorf("Eureka Registration. response:%d body:%s", rc, resp)
		return
	}

	c.sendHeartBeat()
	logger.Info("Eureka Registration.")
}

func (c *EurekaClient) DeRegister() {
	c.stopHeartBeat()

	c.instance.Status = DOWN
	body, _ := json.Marshal(RegisterInstanceJson{c.instance})
	req, _ := http.NewRequest(http.MethodPost, c.serverURL, bytes.NewReader(body))
	_, _, err := c.sendHttpRequest(req)
	if err != nil {
		logger.Error("Eureka DeRegister. error:", err)
		return
	}

	// Deregister
	req, _ = http.NewRequest(http.MethodDelete, c.serverURL+"/"+c.instance.InstanceId, nil)
	_, _, err = c.sendHttpRequest(req)
	if err != nil {
		logger.Error("Eureka DeRegister. error:", err)
		return
	}

	logger.Info("Eureka Deregister.")
}

func (c *EurekaClient) sendHeartBeat() {
	interval := viper.GetInt("eureka.heartbeatInterval")
	c.ticker = time.NewTicker(time.Duration(interval) * time.Second)
	c.done = make(chan bool, 1)

	go func() {
		defer func() {
			logger.Error("stop SendHeartBeat")
			c.ticker.Stop()
		}()

		for {
			select {
			case <-c.ticker.C:
				rc, err := c.heartbeat()
				if err != nil {
					logger.Error(err)
				}
				if rc == http.StatusNotFound {
					logger.Error(err)
				}
			case <-c.done:
				return
			}
		}
	}()
}

func (c *EurekaClient) heartbeat() (int, error) {
	req, _ := http.NewRequest(http.MethodPut, c.serverURL+"/"+c.instance.InstanceId, nil)
	_, rc, err := c.sendHttpRequest(req)
	if err != nil {
		return -1, err
	}
	return rc, nil
}

func (c *EurekaClient) stopHeartBeat() {
	if c.done != nil {
		close(c.done)
	}
}

var HttpClient = &http.Client{
	Transport: transport,
	Timeout:   10 * time.Second,
}

var transport = &http.Transport{
	Dial: (&net.Dialer{
		Timeout: 3 * time.Second,
	}).Dial,
	ResponseHeaderTimeout: 3 * time.Second,
}

func (c *EurekaClient) sendHttpRequest(req *http.Request) ([]byte, int, error) {
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("Accept", "application/json")

	resp, err := HttpClient.Do(req)
	if err != nil {
		return nil, -1, err
	}
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, -1, err
	}

	logger.Debug(string(body))

	return body, resp.StatusCode, nil
}
