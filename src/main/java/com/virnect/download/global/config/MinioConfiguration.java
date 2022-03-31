package com.virnect.download.global.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.minio.MinioClient;

@Profile({"local", "develop", "onpremise", "test","freezing"})
@Configuration
public class MinioConfiguration {
	@Value("${minio.access-key:none}")
	private String accessKey;

	@Value("${minio.secret-key:none}")
	private String secretKey;

	@Value("${minio.server:none}")
	private String minioServerUrl;

	@Bean
	public MinioClient minioClient() throws NoSuchAlgorithmException, KeyManagementException {
		MinioClient minioClient = MinioClient.builder()
			.credentials(accessKey, secretKey)
			.endpoint(minioServerUrl)
			.build();
		minioClient.ignoreCertCheck();
		return minioClient;
	}
}
