package com.NewYearBlessings.vo;

import lombok.Data;

/**
 * 审核结果VO
 */
@Data
public class AuditResultVO {
    private boolean pass;
    private String reason;
    private Double emotionScore;
    private boolean compliant;
}