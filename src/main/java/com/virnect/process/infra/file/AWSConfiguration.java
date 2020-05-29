package com.virnect.process.infra.file;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.05.10
 */
@Profile({"local", "develop", "staging", "production"})
@Configuration
public class AWSConfiguration {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
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
        return AmazonS3ClientBuilder.standard().withCredentials(awsCredentialsProvider).withRegion(Regions.AP_NORTHEAST_2).build();
    }
}
