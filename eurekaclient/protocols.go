package eurekaclient

import "encoding/json"

type StatusType string

const (
	UP           StatusType = "UP"
	DOWN         StatusType = "DOWN"
	STARTING     StatusType = "STARTING"
	OUTOFSERVICE StatusType = "OUT_OF_SERVICE"
	UNKNOWN      StatusType = "UNKNOWN"
)

const (
	MyOwn = "MyOwn"
)

type RegisterInstanceJson struct {
	Instance *Instance `json:"instance"`
}

type Instance struct {
	InstanceId       string `xml:"instanceId" json:"instanceId"`
	HostName         string `xml:"hostName" json:"hostName"`
	App              string `xml:"app" json:"app"`
	IPAddr           string `xml:"ipAddr" json:"ipAddr"`
	VipAddress       string `xml:"vipAddress" json:"vipAddress"`
	SecureVipAddress string `xml:"secureVipAddress" json:"secureVipAddress"`

	Status           StatusType `xml:"status" json:"status"`
	Overriddenstatus StatusType `xml:"overriddenstatus" json:"overriddenstatus"`

	Port              int  `xml:"-" json:"-"`
	PortEnabled       bool `xml:"-" json:"-"`
	SecurePort        int  `xml:"-" json:"-"`
	SecurePortEnabled bool `xml:"-" json:"-"`

	HomePageUrl    string `xml:"homePageUrl" json:"homePageUrl"`
	StatusPageUrl  string `xml:"statusPageUrl" json:"statusPageUrl"`
	HealthCheckUrl string `xml:"healthCheckUrl" json:"healthCheckUrl"`

	CountryId      int64          `xml:"countryId" json:"countryId"`
	DataCenterInfo DataCenterInfo `xml:"dataCenterInfo" json:"dataCenterInfo"`

	LeaseInfo LeaseInfo        `xml:"leaseInfo" json:"leaseInfo"`
	Metadata  InstanceMetadata `xml:"metadata" json:"metadata"`
}

type InstanceMetadata struct {
	Raw    []byte `xml:",innerxml" json:"-"`
	parsed map[string]interface{}
}

type DataCenterInfo struct {
	Name              string
	Class             string
	AlternateMetadata map[string]string
}

type LeaseInfo struct {
	RenewalIntervalInSecs int32 `xml:"renewalIntervalInSecs" json:"renewalIntervalInSecs"`
	DurationInSecs        int32 `xml:"durationInSecs" json:"durationInSecs"`
	RegistrationTimestamp int64 `xml:"registrationTimestamp" json:"registrationTimestamp"`
	LastRenewalTimestamp  int64 `xml:"lastRenewalTimestamp" json:"lastRenewalTimestamp"`
	EvictionTimestamp     int64 `xml:"evictionTimestamp" json:"evictionTimestamp"`
	ServiceUpTimestamp    int64 `xml:"serviceUpTimestamp" json:"serviceUpTimestamp"`
}

func (i *Instance) MarshalJSON() ([]byte, error) {
	// Preclude recursive calls to MarshalJSON.
	type instance Instance
	// outboundJSONFormatPort describes an instance's network port, including whether its registrant
	// considers the port to be enabled or disabled.
	//
	// Example JSON encoding:
	//
	//   "port":{"@enabled":"true", "$":"7101"}
	//
	// Note that later versions of Eureka write the port number as a JSON number rather than as a
	// decimal-formatted string. We emit the port number as a string, not knowing the Eureka
	// server's version. Strangely, the "@enabled" field remains a string.
	type outboundJSONFormatPort struct {
		Number  int  `json:"$,string"`
		Enabled bool `json:"@enabled,string"`
	}
	aux := struct {
		*instance
		Port       outboundJSONFormatPort `json:"port"`
		SecurePort outboundJSONFormatPort `json:"securePort"`
	}{
		(*instance)(i),
		outboundJSONFormatPort{i.Port, i.PortEnabled},
		outboundJSONFormatPort{i.SecurePort, i.SecurePortEnabled},
	}
	return json.Marshal(&aux)
}

// MarshalJSON is a custom JSON marshaler for InstanceMetadata.
func (i *InstanceMetadata) MarshalJSON() ([]byte, error) {
	if i.parsed != nil {
		return json.Marshal(i.parsed)
	}

	if i.Raw == nil {
		i.Raw = []byte("{}")
	}

	return i.Raw, nil
}

func (i *DataCenterInfo) MarshalJSON() ([]byte, error) {
	type named struct {
		Name  string `json:"name"`
		Class string `json:"@class"`
	}
	class := "com.netflix.appinfo.MyDataCenterInfo"
	if i.Name != MyOwn {
		class = i.Class
	}
	return json.Marshal(struct {
		named
		Metadata map[string]string `json:"metadata,omitempty"`
	}{
		named{i.Name, class},
		i.AlternateMetadata,
	})
}
