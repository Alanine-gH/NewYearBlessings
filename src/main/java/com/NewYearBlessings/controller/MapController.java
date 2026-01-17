package com.NewYearBlessings.controller;

import com.NewYearBlessings.common.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 地图数据控制器
 */
@RestController
@RequestMapping("/map")
@Tag(name = "地图数据", description = "地图相关接口")
@Slf4j
public class MapController {

    // 阿里云地图API地址
    private static final String MAP_API_URL = "https://geo.datav.aliyun.com/areas_v3/bound/100000_full_city.json";
    // 高德地图逆地理编码API
    private static final String AMAP_REGEO_URL = "https://restapi.amap.com/v3/geocode/regeo";
    private static final String AMAP_KEY = "7c3e7be4eb3df80d3a1dfaf7193ee308";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public MapController(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取中国地图数据
     */
    @GetMapping("/china")
    @Operation(summary = "获取中国地图数据")
    public R<Object> getChinaMap() {
        try {
            // 调用阿里云地图API获取数据
            Object mapData = restTemplate.getForObject(MAP_API_URL, Object.class);
            return R.success(mapData);
        } catch (Exception e) {
            log.error("获取地图数据失败: {}", e.getMessage());
            return R.error("获取地图数据失败: " + e.getMessage());
        }
    }

    /**
     * 根据经纬度获取城市名称
     */
    @GetMapping("/city")
    @Operation(summary = "根据经纬度获取城市名称")
    public R<String> getCityByCoordinates(
            @RequestParam Double longitude,
            @RequestParam Double latitude) {
        try {
            String url = String.format("%s?location=%s,%s&key=%s&extensions=base",
                    AMAP_REGEO_URL, longitude, latitude, AMAP_KEY);
            
            log.info("调用高德API: {}", url);
            String response = restTemplate.getForObject(url, String.class);
            log.info("高德API响应: {}", response);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("status") && "1".equals(jsonNode.get("status").asText())) {
                if (jsonNode.has("regeocode") && jsonNode.get("regeocode").has("addressComponent")) {
                    JsonNode addressComponent = jsonNode.get("regeocode").get("addressComponent");
                    String city = null;
                    
                    if (addressComponent.has("city") && !addressComponent.get("city").isNull()) {
                        city = addressComponent.get("city").asText();
                    }
                    
                    // 如果city为空，使用province
                    if (city == null || city.isEmpty()) {
                        if (addressComponent.has("province") && !addressComponent.get("province").isNull()) {
                            city = addressComponent.get("province").asText();
                        }
                    }
                    
                    if (city != null && !city.isEmpty()) {
                        log.info("获取城市成功: {}", city);
                        return R.success(city);
                    }
                }
            }
            
            String errorInfo = jsonNode.has("info") ? jsonNode.get("info").asText() : "未知错误";
            log.warn("高德API返回异常: {}", errorInfo);
            return R.error("无法获取城市信息: " + errorInfo);
        } catch (Exception e) {
            log.error("获取城市失败: {}", e.getMessage(), e);
            return R.error("获取城市失败: " + e.getMessage());
        }
    }
}