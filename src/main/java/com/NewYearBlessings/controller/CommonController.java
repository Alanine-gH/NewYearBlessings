package com.NewYearBlessings.controller;

import com.NewYearBlessings.common.R;
import com.NewYearBlessings.config.MinioConfig;
import com.NewYearBlessings.config.MinioUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传和下载控制器
 *
 * @author Alanine
 */
@CrossOrigin
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Autowired
    private MinioUtils minioUtils;
    @Autowired
    private MinioConfig minioConfig;
    
    // 高德地图配置
    @Value("${newyear-blessings.amap.key}")
    private String amapKey;
    
    @Value("${newyear-blessings.amap.security-js-code}")
    private String amapSecurityJsCode;
    
    @Value("${newyear-blessings.amap.callback}")
    private String amapCallback;

    //接收文件对象
    //1.必须用MultipartFile 来接收
    //2.文件名必须与file参数名一致
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            //1.获取传递的文件名称
            String filename = file.getOriginalFilename();
            //1.1 将传递的文件名进行截取 然后加工
            System.err.println(filename);
            String str = StringUtils.substringAfterLast(filename, ".");
            System.out.println("str = " + str);
            String rawFileName = UUID.randomUUID().toString() + "." + str;
            System.err.println(rawFileName);
            //2.将指定文件传入到minio服务器上
            minioUtils.uploadFile(minioConfig.getBucketName(), file, rawFileName, file.getContentType());
            System.out.println("上传成功");
            return R.success(rawFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败");
        }
    }

    /**
     * 文件下载
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/download")
    public void download(@RequestParam("name") String fileName, HttpServletResponse response) throws Exception {
        try {
            InputStream fileInputStream = minioUtils.getObject(
                    minioConfig.getBucketName(), fileName);
            IOUtils.copy(fileInputStream, response.getOutputStream());
        } catch (Exception e) {
            System.out.println("下载失败");
            throw new Exception(e.getMessage() + "下载失败");
        }
    }
    
    /**
     * 获取高德地图配置
     * @return 高德地图配置信息
     */
    @GetMapping("/config/amap")
    public R<Map<String, String>> getAmapConfig() {
        try {
            Map<String, String> configMap = new HashMap<>();
            configMap.put("key", amapKey);
            configMap.put("securityJsCode", amapSecurityJsCode);
            configMap.put("callback", amapCallback);
            return R.success(configMap);
        } catch (Exception e) {
            log.error("获取高德地图配置失败", e);
            return R.error("获取高德地图配置失败");
        }
    }
}
