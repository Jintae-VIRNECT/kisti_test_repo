package com.virnect.workspace.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.minio.MinioClient;
import okhttp3.HttpUrl;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile({"local", "develop", "onpremise"})
@Configuration
public class MinioConfiguration {

	@Value("${minio.access-key}")
	private String accessKey;

	@Value("${minio.secret-key}")
	private String secretKey;

	@Value("${minio.server}")
	private String minioServer;

	@Bean
	public MinioClient minioClient() {
		MinioClient minioClient = MinioClient.builder()
			.endpoint(HttpUrl.parse(minioServer))
			.credentials(accessKey, secretKey)
			.build();
		return minioClient;
	}
}
