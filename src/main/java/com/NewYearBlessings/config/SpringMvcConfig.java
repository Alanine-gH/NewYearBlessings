package com.NewYearBlessings.config;

import com.NewYearBlessings.common.JacksonObjectMapper;
import com.NewYearBlessings.interceptor.CsrfInterceptor;
import com.NewYearBlessings.interceptor.RateLimitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring MVC配置类
 */
@Slf4j
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {
    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;
    @Autowired
    private CsrfInterceptor csrfInterceptor;

    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册CSRF拦截器
        registry.addInterceptor(csrfInterceptor)
                .addPathPatterns("/**")
                // 排除静态资源和Swagger接口
                .excludePathPatterns(
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                        "/", "/index.html", "/**/*.html", "/**/*.css", "/**/*.js",
                        "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.svg"
                );
        
        // 注册限流拦截器
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/**")
                // 排除静态资源和Swagger接口
                .excludePathPatterns(
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                        "/", "/index.html", "/**/*.html", "/**/*.css", "/**/*.js",
                        "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/**/*.gif", "/**/*.svg"
                );
    }

    /**
     * 配置消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建Jackson消息转换器
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 设置自定义的ObjectMapper
        converter.setObjectMapper(new JacksonObjectMapper());
        // 将自定义转换器添加到列表首位，优先使用
        converters.add(0, converter);
    }

    /**
     * 配置RestTemplate，用于调用外部API
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
