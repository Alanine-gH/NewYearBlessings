package com.NewYearBlessings.service;

import com.NewYearBlessings.vo.AuditResultVO;

/**
 * DeepSeek API 服务接口
 */
public interface DeepSeekService {
    /**
     * 审核祝福内容
     */
    AuditResultVO auditBlessingContent(String content);

    /**
     * 分析情感得分
     */
    Double analyzeEmotion(String content);

    /**
     * 检查内容合规性
     */
    boolean checkContentCompliance(String content);
}