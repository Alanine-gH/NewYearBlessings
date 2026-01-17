package com.NewYearBlessings.interceptor;

import com.NewYearBlessings.common.R;
import com.NewYearBlessings.service.RateLimitService;
import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 接口限流拦截器
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitService rateLimitService;

    /**
     * 前置处理，检查请求是否被限流
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true: 允许访问, false: 被限流
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取客户端IP地址
        String ipAddress = getClientIp(request);
        // 获取请求路径
        String requestPath = request.getRequestURI();

        // 检查是否被限流
        boolean allowed = rateLimitService.checkRateLimit(ipAddress, requestPath);
        if (!allowed) {
            // 设置响应头
            response.setContentType("application/json;charset=UTF-8");
            // 返回限流响应
            response.getWriter().write(JSON.toJSONString(R.error("请求过于频繁，请稍后重试")));
            return false;
        }

        return true;
    }

    /**
     * 获取客户端真实IP地址
     * @param request 请求对象
     * @return 真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
