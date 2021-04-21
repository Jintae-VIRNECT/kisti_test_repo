package com.virnect.uaa.global.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

import lombok.RequiredArgsConstructor;
import ua_parser.Parser;

import com.virnect.uaa.global.common.ClientUserAgentInformationParser;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description Login Notification Related Configuration Class
 * @since 2020.07.07
 */

@Configuration
@RequiredArgsConstructor
public class GeoIPConfiguration {
	private final ResourceLoader resourceLoader;

	@Bean
	public Parser parser() throws IOException {
		return new Parser();
	}

	@Bean
	public DatabaseReader databaseReader() throws IOException {
		Resource database = resourceLoader.getResource("classpath:maxmind/GeoLite2-City.mmdb");
		return new DatabaseReader.Builder(database.getInputStream()).withCache(new CHMCache())
			.build();
	}

	@Bean
	public ClientUserAgentInformationParser clientUserAgentInformationParser(
		Parser parser, DatabaseReader databaseReader
	) {
		return new ClientUserAgentInformationParser(parser, databaseReader);
	}
}

