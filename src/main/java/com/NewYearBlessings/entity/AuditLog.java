package com.NewYearBlessings.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 审核日志表
 */
@Data
@TableName("audit_log")
public class AuditLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long blessingId;
    private Integer auditType;
    private Integer auditResult;
    private String auditReason;
    private String deepseekRequest;
    private String deepseekResponse;
    private Double emotionScore;
    private Integer auditDuration;
    private LocalDateTime auditTime;
}