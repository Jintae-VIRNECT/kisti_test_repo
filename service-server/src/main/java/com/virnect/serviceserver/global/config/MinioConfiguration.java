package com.virnect.serviceserver.global.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import io.minio.MinioClient;

@Profile(value = {"local", "develop", "onpremise", "freezing"})
@Configuration
public class MinioConfiguration {
	@Value("${storage.access-key}")
	private String accessKey;
	@Value("${storage.secret-key}")
	private String secretKey;
	@Value("${storage.serverUrl}")
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
