package com.NewYearBlessings.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 接口访问限流记录表
 */
@Data
@TableName("rate_limit_record")
public class RateLimitRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ipAddress;
    private String apiPath;
    private Integer requestCount;
    private LocalDateTime lastRequestTime;
    private Boolean isBlocked;
    private LocalDateTime blockExpireTime;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}