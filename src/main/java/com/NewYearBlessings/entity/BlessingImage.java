package com.NewYearBlessings.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图片资源表
 */
@Data
@TableName("blessing_image")
public class BlessingImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long blessingId;
    private String imageName;
    private String originalName;
    private Long imageSize;
    private String imageFormat;
    private String minioBucket;
    private String minioPath;
    private Integer accessCount;
    private Boolean isDeleted;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}