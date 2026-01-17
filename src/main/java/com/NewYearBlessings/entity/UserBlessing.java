package com.NewYearBlessings.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户祝福表（核心业务表）
 */
@Data
@TableName("user_blessing")
public class UserBlessing {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String blessingContent;
    private String imageName;
    private String city;
    private Integer auditStatus;
    private String auditReason;
    private Double emotionScore;
    private String submitIp;
    private LocalDateTime submitTime;
    private LocalDateTime auditTime;
    private Boolean isDeleted;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}