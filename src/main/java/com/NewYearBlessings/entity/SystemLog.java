package com.NewYearBlessings.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统操作日志表
 */
@Data
@TableName("system_log")
public class SystemLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String logType;
    private String logLevel;
    private String moduleName;
    private String operation;
    private String requestUrl;
    private String requestMethod;
    private String requestParams;
    private String responseCode;
    private String errorMessage;
    private String ipAddress;
    private String userAgent;
    private Integer executionTime;
    private LocalDateTime createdTime;
}