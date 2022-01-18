package com.virnect.uaa.global.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.minio.MinioClient;

@Profile(value = {"dev", "local", "develop", "onpremise", "test"})
@Configuration
public class MinioConfiguration {
	@Value("${minio.access-key}")
	private String accessKey;
	@Value("${minio.secret-key}")
	private String secretKey;
	@Value("${minio.server}")
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
