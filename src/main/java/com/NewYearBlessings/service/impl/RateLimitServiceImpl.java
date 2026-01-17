package com.NewYearBlessings.service.impl;

import com.NewYearBlessings.entity.RateLimitRecord;
import com.NewYearBlessings.mapper.RateLimitRecordMapper;
import com.NewYearBlessings.service.RateLimitService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 接口限流服务实现类
 */
@Service
public class RateLimitServiceImpl implements RateLimitService {

    @Autowired
    private RateLimitRecordMapper rateLimitRecordMapper;

    @Value("${newyear-blessings.rate-limit.per-minute:10}")
    private int rateLimitPerMinute;

    /**
     * 检查IP是否被限流
     * @param ipAddress IP地址
     * @param apiPath 接口路径
     * @return true: 允许访问, false: 被限流
     */
    @Override
    public boolean checkRateLimit(String ipAddress, String apiPath) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinuteAgo = now.minusMinutes(1);

        // 查询该IP在过去一分钟内的请求次数
        QueryWrapper<RateLimitRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip_address", ipAddress)
                .eq("api_path", apiPath)
                .ge("last_request_time", oneMinuteAgo)
                .ge("created_time", oneMinuteAgo);

        Long count = rateLimitRecordMapper.selectCount(queryWrapper);

        if (count >= rateLimitPerMinute) {
            return false; // 超过限流阈值，拒绝访问
        }

        // 使用INSERT ... ON DUPLICATE KEY UPDATE处理并发插入问题
        // 先尝试删除旧记录（超过1分钟的记录）
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ip_address", ipAddress)
                .eq("api_path", apiPath)
                .lt("last_request_time", oneMinuteAgo);
        rateLimitRecordMapper.delete(queryWrapper);

        // 插入或更新记录
        RateLimitRecord record = new RateLimitRecord();
        record.setIpAddress(ipAddress);
        record.setApiPath(apiPath);
        record.setRequestCount(1);
        record.setLastRequestTime(now);
        record.setIsBlocked(false);
        record.setCreatedTime(now);
        record.setUpdatedTime(now);

        try {
            // 尝试插入新记录
            rateLimitRecordMapper.insert(record);
        } catch (Exception e) {
            // 如果插入失败（唯一约束冲突），则更新现有记录
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ip_address", ipAddress)
                    .eq("api_path", apiPath);
            
            record = rateLimitRecordMapper.selectOne(queryWrapper);
            if (record != null) {
                // 如果记录存在且在1分钟内，则更新计数
                if (record.getLastRequestTime().isAfter(oneMinuteAgo)) {
                    record.setRequestCount(record.getRequestCount() + 1);
                    record.setLastRequestTime(now);
                    record.setUpdatedTime(now);
                    rateLimitRecordMapper.updateById(record);
                } else {
                    // 如果记录已超过1分钟，则重置计数
                    record.setRequestCount(1);
                    record.setLastRequestTime(now);
                    record.setUpdatedTime(now);
                    record.setCreatedTime(now);
                    rateLimitRecordMapper.updateById(record);
                }
            }
        }

        return true; // 允许访问
    }
}
