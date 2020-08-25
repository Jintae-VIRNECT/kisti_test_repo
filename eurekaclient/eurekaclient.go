package eurekaclient

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/util"
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"net"
	"net/http"
	"strconv"
	"time"

	"github.com/spf13/viper"
)

type State int

const (
	StateRegistration State = iota
	StateHeartBeat
	StateDeRegistration
)

type EurekaClient struct {
	instance  *Instance
	serverURL string
	ticker    *time.Ticker
	done      chan bool
	ctx       context.Context
	stop      context.CancelFunc
	state     chan State
}

func (c *EurekaClient) Run() {
	if viper.GetBool("eureka.enable") == false {
		logger.Info("eureka client disabled")
		return
	}

	c.ctx, c.stop = context.WithCancel(context.Background())

	go func(c *EurekaClient) {
		c.state = make(chan State, 1)
		c.state <- StateRegistration
		for {
			switch <-c.state {
			case StateRegistration:
				go c.runRegister()
			case StateHeartBeat:
				go c.runHeartBeat()
			case StateDeRegistration:
				go c.runDeRegister()
			}
		}
	}(c)
}

func (c *EurekaClient) Stop() {
	if c.stop == nil {
		return
	}
	c.stop()
}

func (c *EurekaClient) runRegister() {
	logger.Info("eureka state: register")
	ticker := time.NewTicker(3 * time.Second)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			err := c.Register()
			if err == nil {
				c.state <- StateHeartBeat
				return
			}
		case <-c.ctx.Done():
			c.state <- StateDeRegistration
			return
		}
	}
}

func (c *EurekaClient) runHeartBeat() {
	logger.Info("eureka state: heartbeat")
	ticker := time.NewTicker(time.Duration(viper.GetInt("eureka.heartbeatInterval")) * time.Second)
	defer ticker.Stop()
	for {
		select {
		case <-ticker.C:
			err := c.Heartbeat()
			if err != nil {
				c.state <- StateRegistration
				return
			}
		case <-c.ctx.Done():
			c.state <- StateDeRegistration
			return
		}
	}
}

func (c *EurekaClient) runDeRegister() {
	logger.Info("eureka state: deregister")
	c.DeRegister()
}

func NewClient() *EurekaClient {
	port := viper.GetInt("general.port")
	appName := viper.GetString("eureka.app")
	var ipAddr string
	if viper.IsSet("eureka.instanceIp") {
		ipAddr = viper.GetString("eureka.instanceIp")
	} else {
		ipAddr = util.GetLocalIP()
	}
	baseURL := "http://" + ipAddr + ":" + strconv.Itoa(port) + "/"
	c := &EurekaClient{
		serverURL: viper.GetString("eureka.serverURL") + "/apps/" + viper.GetString("eureka.app"),
	}

	c.instance = &Instance{
		InstanceId:        util.GetHostName() + ":" + viper.GetString("eureka.app") + ":" + strconv.Itoa(port),
		HostName:          ipAddr,
		App:               appName,
		IPAddr:            ipAddr,
		Status:            UP,
		Overriddenstatus:  UNKNOWN,
		Port:              port,
		PortEnabled:       true,
		SecurePort:        443,
		SecurePortEnabled: false,
		CountryId:         1,
		DataCenterInfo: DataCenterInfo{
			Name: MyOwn,
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

func (c *EurekaClient) Register() error {
	body, _ := json.Marshal(RegisterInstanceJson{c.instance})
	req, _ := http.NewRequest(http.MethodPost, c.serverURL, bytes.NewReader(body))

	resp, rc, err := c.sendHttpRequest(req)
	if err != nil {
		logger.Error("Eureka Registration. error:", err)
		return err
	}

	if rc != http.StatusNoContent {
		logger.Errorf("Eureka Registration. response:%d body:%s", rc, resp)
		return err
	}

	logger.Info("Eureka Registration.")
	return nil
}

func (c *EurekaClient) DeRegister() {
	// Status "DOWN"
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

func (c *EurekaClient) Heartbeat() error {
	req, _ := http.NewRequest(http.MethodPut, c.serverURL+"/"+c.instance.InstanceId, nil)
	_, rc, err := c.sendHttpRequest(req)
	if err != nil {
		return err
	}
	if rc != 200 {
		return errors.New("eureka server is restarted")
	}
	return nil
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
		logger.Error(string(body))
		return nil, -1, err
	}

	return body, resp.StatusCode, nil
}
