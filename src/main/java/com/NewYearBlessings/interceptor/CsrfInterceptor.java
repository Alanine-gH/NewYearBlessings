package com.NewYearBlessings.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * CSRF防护拦截器
 */
@Component
public class CsrfInterceptor implements HandlerInterceptor {

    /**
     * 前置处理，验证CSRF防护
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return true: 允许访问, false: 拒绝访问
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求方法
        String method = request.getMethod();
        
        // 对于GET、HEAD、OPTIONS等安全方法，不需要CSRF防护
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            return true;
        }
        
        // 获取Origin和Referer头
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        
        // 获取当前请求的主机名
        String serverName = request.getServerName();
        
        // 验证Origin头
        if (origin != null && !origin.isEmpty()) {
            // 检查Origin是否来自可信域名
            if (isTrustedOrigin(origin, serverName)) {
                return true;
            }
        }
        
        // 验证Referer头
        if (referer != null && !referer.isEmpty()) {
            // 检查Referer是否来自可信域名
            if (isTrustedReferer(referer, serverName)) {
                return true;
            }
        }
        
        // 如果都不满足，返回403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 40003, \"msg\": \"CSRF验证失败\"}");
        return false;
    }
    
    /**
     * 检查Origin是否来自可信域名
     * @param origin Origin头的值
     * @param serverName 当前服务器主机名
     * @return true: 可信, false: 不可信
     */
    private boolean isTrustedOrigin(String origin, String serverName) {
        // 允许本地开发环境
        if (origin.startsWith("http://localhost") || origin.startsWith("http://127.0.0.1")) {
            return true;
        }
        
        // 允许同源请求
        if (origin.contains(serverName)) {
            return true;
        }
        
        // 可以添加其他可信域名
        return false;
    }
    
    /**
     * 检查Referer是否来自可信域名
     * @param referer Referer头的值
     * @param serverName 当前服务器主机名
     * @return true: 可信, false: 不可信
     */
    private boolean isTrustedReferer(String referer, String serverName) {
        // 允许本地开发环境
        if (referer.startsWith("http://localhost") || referer.startsWith("http://127.0.0.1")) {
            return true;
        }
        
        // 允许同源请求
        if (referer.contains(serverName)) {
            return true;
        }
        
        // 可以添加其他可信域名
        return false;
    }
}
