package com.NewYearBlessings.service.impl;

import com.NewYearBlessings.service.DeepSeekService;
import com.NewYearBlessings.vo.AuditResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import com.alibaba.fastjson2.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * DeepSeek API 服务 实现类
 */
@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Value("${newyear-blessings.deepseek.api-key}")
    private String apiKey;

    @Value("${newyear-blessings.deepseek.base-url}")
    private String baseUrl;

    @Value("${newyear-blessings.deepseek.emotion-threshold}")
    private double emotionThreshold;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public AuditResultVO auditBlessingContent(String content) {
        AuditResultVO result = new AuditResultVO();
        result.setPass(false);

        try {
            // 1. 情感分析
            double emotionScore = analyzeEmotion(content);
            result.setEmotionScore(emotionScore);

            // 2. 内容合规性检查
            boolean compliant = checkContentCompliance(content);
            result.setCompliant(compliant);

            // 3. 综合判断
            if (emotionScore >= emotionThreshold && compliant) {
                result.setPass(true);
                result.setReason("审核通过");
            } else {
                result.setPass(false);
                if (emotionScore < emotionThreshold) {
                    result.setReason("情感不符合要求，需积极乐观的祝福内容");
                } else {
                    result.setReason("内容不符合要求，含有违规信息");
                }
            }
        } catch (Exception e) {
            // API调用失败，使用降级方案
            result.setPass(false);
            result.setReason("内容审核服务暂时不可用，请稍后重试");
            result.setEmotionScore(0.0);
            result.setCompliant(false);
        }

        return result;
    }

    @Override
    public Double analyzeEmotion(String content) {
        try {
            // 构建请求
            String url = baseUrl + "/chat/completions";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            
            // 构建情感分析提示
            String prompt = "请分析以下新年祝福内容的情感积极程度，仅返回0-100之间的数字，0表示非常消极，100表示非常积极，不要包含其他任何内容：\"" + content + "\"";
            
            // 构建messages
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个情感分析专家，擅长分析文本的情感积极程度。");
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            
            requestBody.put("messages", new Object[]{systemMessage, userMessage});
            requestBody.put("temperature", 0.0);
            requestBody.put("max_tokens", 10);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // 解析响应
            JSONObject responseJson = JSONObject.parseObject(response.getBody());
            String result = responseJson.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content").trim();
            
            // 提取数字
            return Double.parseDouble(result);
        } catch (Exception e) {
            // API调用失败，返回默认值
            return 0.0;
        }
    }

    @Override
    public boolean checkContentCompliance(String content) {
        try {
            // 构建请求
            String url = baseUrl + "/chat/completions";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            
            // 构建内容审核提示
            String prompt = "请判断以下新年祝福内容是否合规，仅返回true或false，true表示合规，false表示违规，不要包含其他任何内容：\"" + content + "\"";
            
            // 构建messages
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个内容审核专家，擅长判断文本是否合规。合规的内容应该是积极、健康、符合新年祝福场景的，不包含违规违法、色情暴力、歧视侮辱等信息。");
            
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            
            requestBody.put("messages", new Object[]{systemMessage, userMessage});
            requestBody.put("temperature", 0.0);
            requestBody.put("max_tokens", 10);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // 解析响应
            JSONObject responseJson = JSONObject.parseObject(response.getBody());
            String result = responseJson.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content").trim();
            
            return Boolean.parseBoolean(result);
        } catch (Exception e) {
            // API调用失败，返回默认值
            return false;
        }
    }
}