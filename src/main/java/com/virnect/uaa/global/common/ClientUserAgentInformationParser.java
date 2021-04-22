package com.virnect.uaa.global.common;

import static java.util.Objects.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua_parser.Client;
import ua_parser.Parser;

import com.virnect.uaa.domain.auth.account.dto.ClientGeoIPInfo;
import com.virnect.uaa.domain.auth.account.dto.GeoIPInfo;

@Slf4j
@RequiredArgsConstructor
public class ClientUserAgentInformationParser {
	private static final String UNKNOWN = "UNKNOWN";
	private final Parser parser;
	private final DatabaseReader databaseReader;

	/**
	 * 접속 국가 및 위치 정보 추출
	 *
	 * @param request - http 요청 정보
	 * @return
	 */
	public ClientGeoIPInfo getClientGeoIPInformation(HttpServletRequest request) {
		String ip = extractIpFromRequest(request);
		GeoIPInfo geoIPInfo = getUserLocationByIp(ip);
		String userAgent = request.getHeader("user-agent");
		String deviceDetails = getDeviceDetails(userAgent);

		ClientGeoIPInfo clientGeoIPInfo = new ClientGeoIPInfo();
		clientGeoIPInfo.setIp(ip);
		clientGeoIPInfo.setLocation(geoIPInfo.getLocation());
		clientGeoIPInfo.setCountry(geoIPInfo.getCountry());
		clientGeoIPInfo.setCountryCode(geoIPInfo.getCountryCode());
		clientGeoIPInfo.setUserAgent(userAgent);
		clientGeoIPInfo.setDeviceDetails(deviceDetails);
		return clientGeoIPInfo;
	}

	/**
	 * Ip 기반으로 Geolocation 정보 추출
	 *
	 * @param ip - 클라이언트 IP
	 * @return - Geolocation 정보 (ip, location, country, countryCode)
	 */
	public GeoIPInfo getUserLocationByIp(String ip) {
		String location = UNKNOWN;
		String country = UNKNOWN;
		String countryCode = UNKNOWN;

		try {
			InetAddress ipAddress = InetAddress.getByName(ip);
			CityResponse cityResponse = databaseReader.city(ipAddress);
			if (Objects.nonNull(cityResponse) && Objects.nonNull(cityResponse.getCity()) && !Strings
				.isNullOrEmpty(cityResponse.getCity().getName()) && Objects
				.nonNull(cityResponse.getCountry()) && !Strings
				.isNullOrEmpty(cityResponse.getCountry().getName())) {
				location = cityResponse.getCountry().getName();
				location += " - " + cityResponse.getCity().getName();
				country = cityResponse.getCountry().getName();
				countryCode = cityResponse.getCountry().getIsoCode();
			}
		} catch (IOException | GeoIp2Exception e) {
			log.error(e.getMessage());
		}

		return new GeoIPInfo(ip, location, country, countryCode);
	}

	/**
	 * Client IP 추출
	 *
	 * @param request - client request
	 * @return - client IP address
	 */
	public String extractIpFromRequest(HttpServletRequest request) {
		String clientIp;
		String clientXForwardedForIp = request
			.getHeader("x-forwarded-for");
		if (nonNull(clientXForwardedForIp)) {
			clientIp = parseXForwardedHeader(clientXForwardedForIp);
		} else {
			clientIp = request.getRemoteAddr();
		}
		return clientIp;
	}

	/**
	 * Proxy request ip 추출
	 *
	 * @param header - request header
	 * @return - ip address
	 */
	private String parseXForwardedHeader(String header) {
		return header.split(" *, *")[0];
	}

	/**
	 * User Agent 기반으로 Client 기기 정보 추출
	 *
	 * @param userAgent
	 * @return
	 */
	public String getDeviceDetails(String userAgent) {
		String deviceDetails = UNKNOWN;

		Client client = parser.parse(userAgent);
		if (Objects.nonNull(client)) {
			log.info("CLIENT INFO : {}", client.toString());
			deviceDetails =
				client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
					" - " + client.os.family + " " + client.os.major + "." + client.os.minor;
		}

		return deviceDetails;
	}

}
