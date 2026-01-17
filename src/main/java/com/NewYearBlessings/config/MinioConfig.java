package com.NewYearBlessings.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置类
 */
@Data
@Configuration
public class MinioConfig {
    /**
     * 访问地址
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * accessKey类似于用户ID，用于唯一标识你的账户
     */
    @Value("${minio.access-key}")
    private String accessKey;

    /**
     * secretKey是你账户的密码
     */
    @Value("${minio.secret-key}")
    private String secretKey;
    /**
     * 默认存储桶
     */
    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.port}")
    private int port;

    @Value("${minio.secure}")
    private boolean secure;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint, port, secure)
                .credentials(accessKey, secretKey).build();
        return minioClient;
    }
}
