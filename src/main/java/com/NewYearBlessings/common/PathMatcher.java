package com.NewYearBlessings.common;

import org.springframework.util.AntPathMatcher;

/**
 * 路径匹配器，用于判断请求路径是否需要处理
 *
 * @author Alanine
 */
public class PathMatcher {

    //定义不需要处理的请求路径
    public static String[] URLS = new String[]{
//            "/**",
            "/captcha",
            "/auth/login",
            "/back/**",
            "/show/**",
            "/user/sendMsg",
            "/alipay/**",
            "/doc.html",
            "/webjars/**",
            "/swagger-resources",
            "/v2/api-docs",
            "/common/**",
    };
    /*采用默认*/
    /**
     * 匹配请求路径是否需要处理
     *
     * @param requestURI
     * @return 是否需要处理
     */
    public static boolean check(String requestURI) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String url : URLS) {
            boolean match = antPathMatcher.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

    /**
     * 参数匹配器
     *
     * @param urls       名单数组
     * @param requestURI 需要验证的网址
     * @return 例如：
     * /a/*匹配a前缀一层目录
     * /a/** 匹配a前缀多级目录
     */
    public static boolean check(String[] urls, String requestURI) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String url : urls) {
            boolean match = antPathMatcher.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
