package com.NewYearBlessings.service;

/**
 * 接口限流服务
 */
public interface RateLimitService {
    /**
     * 检查IP是否被限流
     * @param ipAddress IP地址
     * @param apiPath 接口路径
     * @return true: 允许访问, false: 被限流
     */
    boolean checkRateLimit(String ipAddress, String apiPath);
}
