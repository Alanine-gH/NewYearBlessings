package com.NewYearBlessings.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.Map;

/**
 * 图片服务接口
 */
public interface ImageService {
    /**
     * 上传图片
     */
    String uploadImage(MultipartFile file, Long blessingId) throws Exception;

    /**
     * 获取图片输入流
     */
    InputStream getImage(String imageName) throws Exception;

    /**
     * 删除图片
     */
    boolean deleteImage(String imageName);

    /**
     * 获取图片URL
     */
    String getImageUrl(String imageName);

    /**
     * 验证图片格式和大小
     */
    Map<String, Object> validateImage(MultipartFile file);
}