package com.virnect.message.global.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Project: PF-Message
 * DATE: 2020-02-13
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */

@Configuration
@Profile({"production", "staging"})
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
    /**
     * Amazon SES sync Client
     *
     * @param awsCredentialsProvider - AWS Credentials
     * @return Amazon SES Async Client Instance
     */
    @Bean
    public AmazonSimpleEmailServiceClient amazonSimpleEmailServiceClient(AWSCredentialsProvider awsCredentialsProvider) {
        return (AmazonSimpleEmailServiceClient) AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(awsCredentialsProvider).withRegion(Regions.US_WEST_2).build();
    }

    @Bean
    public AmazonSimpleEmailServiceAsyncClient amazonSimpleEmailServiceAsyncClient(AWSCredentialsProvider awsCredentialsProvider){
        return (AmazonSimpleEmailServiceAsyncClient) AmazonSimpleEmailServiceAsyncClientBuilder.standard().withCredentials(awsCredentialsProvider).withRegion(Regions.US_WEST_2).build();
    }

}
