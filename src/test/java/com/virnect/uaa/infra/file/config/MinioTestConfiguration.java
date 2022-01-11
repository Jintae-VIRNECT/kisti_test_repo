package com.virnect.uaa.infra.file.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import io.minio.MinioClient;

import com.virnect.uaa.infra.file.MinioFileService;

@TestConfiguration
public class MinioTestConfiguration {
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

	@Bean
	public MinioFileService minioFileService(MinioClient minioClient) {
		return new MinioFileService(minioClient);
	}
}
