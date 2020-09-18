package main

import (
	"RM-RecordServer/logger"
	"fmt"
	"net/http"
	"net/url"
	"os"
	"path"

	"github.com/spf13/pflag"
	"github.com/spf13/viper"
	"gopkg.in/yaml.v2"
)

func readConfigFromURL() error {
	configServer, ok := os.LookupEnv("CONFIG_SERVER")
	if !ok {
		return fmt.Errorf("not found env: CONFIG_SERVER")
	}

	env, ok := os.LookupEnv("VIRNECT_ENV")
	if !ok {
		return fmt.Errorf("not found env: VIRNECT_ENV")
	}

	u, _ := url.Parse(configServer)
	configFilenamePath := fmt.Sprintf("/record-server/%s/master/record-server-%s.ini", env, env)
	u.Path = path.Join(u.Path, configFilenamePath)
	fmt.Println("configure: performing http get... to", u)
	resp, err := http.Get(u.String())
	if err != nil {
		return fmt.Errorf("get fail: %s", err)
	}

	defer resp.Body.Close()

	viper.SetConfigType("toml")
	err = viper.ReadConfig(resp.Body)
	if err != nil {
		return fmt.Errorf("fatal error config: %s", err)
	}

	return nil
}

func readConfigFromFile(configPath string) error {
	viper.SetConfigType("toml")
	viper.SetConfigName(configPath)
	viper.AddConfigPath(".")
	err := viper.ReadInConfig() // Find and read the config file
	if err != nil {             // Handle errors reading the config file
		return fmt.Errorf("fatal error config file: %s", err)
	}

	return nil
}

func readConfig() {
	var configPath string
	var logStdout bool
	var version bool
	pflag.BoolVarP(&version, "version", "v", false, "show version")
	pflag.StringVarP(&configPath, "config", "c", "", "path to config file")
	pflag.BoolVarP(&logStdout, "stdout", "s", false, "only output to stdout")

	pflag.Parse()

	if version {
		fmt.Printf("version: %s-%s\n", Version, Build)
		os.Exit(0)
	}

	// read configuration from file or config-server
	if len(configPath) > 0 {
		if err := readConfigFromFile(configPath); err != nil {
			panic(err)
		}
	} else {
		if err := readConfigFromURL(); err != nil {
			panic(err)
		}
	}

	if logStdout {
		viper.Set("log.stdout", true)
	}

	if instanceIP, ok := os.LookupEnv("EUREKA_INSTANCE_IP"); ok {
		viper.Set("eureka.instanceIp", instanceIP)
	}

	var recDir string
	if _, err := os.Stat("/.dockerenv"); err == nil {
		recDir = viper.GetString("record.dirOnDocker")
	} else {
		recDir = viper.GetString("record.dirOnHost")
	}
	viper.Set("record.dir", recDir)
}

func displayConfig() {
	// show all settings
	bs, _ := yaml.Marshal(viper.AllSettings())
	logger.Info("settings\n", string(bs))
}
