package com.virnect.uaa.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description GeoIP Service Test Class
 * @since 2020.04.09
 */

// @SpringBootTest
// @ActiveProfiles("local")
@Slf4j
public class GetGeoIPInfoTest {
	private static String IP = "121.162.252.234";
	// @Autowired
	// private DatabaseReader reader;

	// @Test
	// public void 아이피로_나라_도시_찾기() {
	//
	// 	String location = "UNKNOWN";
	// 	try {
	// 		InetAddress ipAddress = InetAddress.getByName(IP);
	// 		CityResponse cityResponse = reader.city(ipAddress);
	// 		if (Objects.nonNull(cityResponse) && Objects.nonNull(cityResponse.getCity()) && !Strings.isNullOrEmpty(
	// 			cityResponse.getCity().getName()) && Objects.nonNull(cityResponse.getCountry())
	// 			&& !Strings.isNullOrEmpty(cityResponse.getCountry().getName())) {
	// 			location = cityResponse.getCountry().getNames().get("ko_KR");
	// 			location += " - " + cityResponse.getCity().getNames().get("ko_KR");
	// 		}
	// 	} catch (IOException | GeoIp2Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	log.info("LOCATION: {}", location);
	// }

	@Test
	public void 시간대_테스트() {
		// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY.MM.DD HH:mm `GMT`+z");
		// DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY.MM.DD HH:mm")

		// 	.withZone(ZoneId.of("Asia/Seoul"));

		// ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		// System.out.println(zonedDateTime.format(DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm z")));
		// System.out.println(
		// 	String.format("%s GMT+9:00",
		// 		LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm"))));
		// System.out.println(LocalDateTime.now().format(dateTimeFormatter));
		LocalDateTime localDateTime = LocalDateTime.now();
		System.out.println(localDateTime.format(DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm")));
		System.out.println(String.format(
			"%s GMT+9:00",
			localDateTime.plusHours(9).format(DateTimeFormatter.ofPattern("YYYY.MM.dd HH:mm"))
		));
	}
}
