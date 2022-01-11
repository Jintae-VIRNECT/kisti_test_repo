package com.virnect.uaa.infra.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import com.virnect.uaa.infra.file.S3FileService;

@TestConfiguration
public class S3TestConfiguration {
	@Value("${cloud.aws.credentials.access-key:none}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key:none}")
	private String secretKey;

	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	@Bean
	public AWSCredentialsProvider awsCredentialsProvider() {
		return new AWSStaticCredentialsProvider(basicAWSCredentials());
	}

	/**
	 * Amazon S3 Client
	 *
	 * @param awsCredentialsProvider - AWS Credential
	 * @return Amazon s3 Client Instance
	 */
	@Bean
	public AmazonS3 amazonS3Client(AWSCredentialsProvider awsCredentialsProvider) {
		return AmazonS3ClientBuilder.standard()
			.withCredentials(awsCredentialsProvider)
			.withRegion(Regions.AP_NORTHEAST_2)
			.build();
	}

	@Bean
	public S3FileService s3FileService(AmazonS3 amazonS3) {
		return new S3FileService(amazonS3);
	}
}
