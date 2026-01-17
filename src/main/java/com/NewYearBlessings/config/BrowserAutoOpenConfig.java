package com.NewYearBlessings.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.net.URI;

/**
 * 浏览器自动打开配置
 * 当Spring Boot应用启动完成后，自动打开浏览器访问应用首页
 */
@Slf4j
@Configuration
public class BrowserAutoOpenConfig implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        // 应用访问地址
        String url = "http://localhost:8080/api";
        
        try {
            // 判断当前系统是否支持Desktop类
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                // 判断是否支持浏览器打开操作
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    log.info("正在打开浏览器访问: {}", url);
                    desktop.browse(new URI(url));
                } else {
                    // 如果不支持Desktop类，则尝试使用命令行打开
                    openBrowserByCommand(url);
                }
            } else {
                // 如果不支持Desktop类，则尝试使用命令行打开
                openBrowserByCommand(url);
            }
        } catch (Exception e) {
            log.error("自动打开浏览器失败: {}", e.getMessage());
        }
    }

    /**
     * 通过命令行打开浏览器
     * @param url 访问地址
     */
    private void openBrowserByCommand(String url) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;
            
            if (os.contains("win")) {
                // Windows系统
                processBuilder = new ProcessBuilder("cmd.exe", "/c", "start", url);
            } else if (os.contains("mac")) {
                // macOS系统
                processBuilder = new ProcessBuilder("open", url);
            } else {
                // Linux系统
                processBuilder = new ProcessBuilder("xdg-open", url);
            }
            
            processBuilder.start();
            log.info("通过命令行打开浏览器访问: {}", url);
        } catch (Exception e) {
            log.error("通过命令行打开浏览器失败: {}", e.getMessage());
        }
    }
}