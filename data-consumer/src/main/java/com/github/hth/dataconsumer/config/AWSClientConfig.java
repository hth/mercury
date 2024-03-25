/*
 * Copyright 2024 the original author hth.
 */
package com.github.hth.dataconsumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSClientConfig {
    @Value("${spring.aws.accessId}")
    private String awsAccessKeyId;

    @Value("${spring.aws.secretId}")
    private String awsSecretKey;

    @Value("${spring.aws.region}")
    private String awsRegion;

    private AwsBasicCredentials awsBasicCredentials() {
        return AwsBasicCredentials.create(awsAccessKeyId, awsSecretKey);
    }

    public StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(awsBasicCredentials());
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder().region(Region.of(awsRegion))
                .credentialsProvider(staticCredentialsProvider())
                .build();
    }
}
