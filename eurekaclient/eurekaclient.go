package eurekaclient

import (
	"RM-RecordServer/logger"
	"RM-RecordServer/util"
	"fmt"
	"strconv"
	"time"

	"github.com/hudl/fargo"
	"github.com/spf13/viper"
)

type EurekaClient struct {
	instanceID string
	config     fargo.Config
	conn       fargo.EurekaConnection
	instance   *fargo.Instance
	ticker     *time.Ticker
	done       chan bool
}

func NewClient() *EurekaClient {
	conf := fargo.Config{}
	conf.Eureka.ServiceUrls = []string{viper.GetString("eureka.serverURL")}
	return &EurekaClient{
		instanceID: util.GetHostName() + ":" + viper.GetString("eureka.app") + ":" + strconv.Itoa(viper.GetInt("general.port")),
		config:     conf}
}

func (c *EurekaClient) Register() {
	c.conn = fargo.NewConnFromConfig(c.config)
	c.conn.UseJson = true

	port := viper.GetInt("general.port")
	appName := viper.GetString("eureka.app")
	baseURL := "http://" + util.GetLocalIP() + ":" + strconv.Itoa(port) + "/"
	c.instance = &fargo.Instance{
		InstanceId:        c.instanceID,
		HostName:          util.GetLocalIP(),
		App:               appName,
		IPAddr:            util.GetLocalIP(),
		Status:            fargo.UP,
		Overriddenstatus:  fargo.UNKNOWN,
		Port:              port,
		PortEnabled:       true,
		SecurePort:        443,
		SecurePortEnabled: false,
		CountryId:         1,
		DataCenterInfo:    fargo.DataCenterInfo{Name: fargo.MyOwn},
		LeaseInfo: fargo.LeaseInfo{
			RenewalIntervalInSecs: 30,
			DurationInSecs:        90,
		},
		HomePageUrl:      baseURL,
		StatusPageUrl:    baseURL + "actuator/info",
		HealthCheckUrl:   baseURL + "actuator/health",
		VipAddress:       appName,
		SecureVipAddress: appName,
	}
	c.instance.SetMetadataString("management.port", strconv.Itoa(port))

	err := c.conn.RegisterInstance(c.instance)
	if err != nil {
		logger.Warn("Eureka Registration fail: ", err)
		return
	}

	c.sendHeartBeat()
	logger.Info("Eureka Registration.")
}

func (c *EurekaClient) DeRegister() {
	c.stopHeartBeat()

	c.instance.Status = fargo.DOWN
	c.conn.RegisterInstance(c.instance)
	err := c.conn.DeregisterInstance(c.instance)
	if err != nil {
		logger.Error("eureka: Deregister:", err)
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
			fmt.Println("stop SendHeartBeat")
			c.ticker.Stop()
		}()

		for {
			select {
			case <-c.ticker.C:
				c.conn.HeartBeatInstance(c.instance)
			case <-c.done:
				return
			}
		}
	}()
}

func (c *EurekaClient) stopHeartBeat() {
	if c.done != nil {
		close(c.done)
	}
}
