package com.NewYearBlessings.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 祝福VO
 */
@Data
public class BlessingVO {
    private Long id;
    private String nickname;
    private String blessingContent;
    private String imageUrl;
    private String city;
    private LocalDateTime submitTime;
}