package com.virnect.uaa.domain.user.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description User last use device metatdata
 * @since 2020.07.07
 */

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_access_log")
public class UserAccessLog extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_access_log_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "device_details")
	private String deviceDetails;

	@Column(name = "user_agent")
	private String userAgent;

	@Column(name = "location")
	private String location;

	@Column(name = "country")
	private String country;

	@Column(name = "country_code", length = 10)
	private String countryCode;

	@Column(name = "ip")
	private String ip;

	@Column(name = "last_logged_in")
	private LocalDateTime lastLoggedIn;

	@Builder
	public UserAccessLog(
		User user, String deviceDetails, String ip, String location, String userAgent, String country,
		String countryCode, LocalDateTime lastLoggedIn
	) {
		this.user = user;
		this.ip = ip;
		this.location = location;
		this.deviceDetails = deviceDetails;
		this.userAgent = userAgent;
		this.lastLoggedIn = lastLoggedIn;
		this.country = country;
		this.countryCode = countryCode;
	}

	public static UserAccessLog ofUserAndClientGeoIPInfo(User user, ClientGeoIPInfo clientGeoIPInfo) {
		UserAccessLog userAccessLog = new UserAccessLog();
		userAccessLog.user = user;
		userAccessLog.ip = clientGeoIPInfo.getIp();
		userAccessLog.location = clientGeoIPInfo.getLocation();
		userAccessLog.deviceDetails = clientGeoIPInfo.getDeviceDetails();
		userAccessLog.userAgent = clientGeoIPInfo.getUserAgent();
		userAccessLog.lastLoggedIn = LocalDateTime.now();
		userAccessLog.country = clientGeoIPInfo.getCountry();
		userAccessLog.countryCode = clientGeoIPInfo.getCountryCode();
		return userAccessLog;
	}

	public String getDevice() {
		String deviceMajorInfo = deviceDetails.split(" ")[0];
		if (deviceMajorInfo.equalsIgnoreCase("okhttp")) {
			return "Mobile";
		}
		return deviceMajorInfo;
	}

	@Override
	public String toString() {
		return "UserAccessLog{" +
			"id=" + id +
			", user=" + user +
			", deviceDetails='" + deviceDetails + '\'' +
			", userAgent='" + userAgent + '\'' +
			", location='" + location + '\'' +
			", country='" + country + '\'' +
			", countryCode='" + countryCode + '\'' +
			", ip='" + ip + '\'' +
			", lastLoggedIn=" + lastLoggedIn +
			'}';
	}
}
