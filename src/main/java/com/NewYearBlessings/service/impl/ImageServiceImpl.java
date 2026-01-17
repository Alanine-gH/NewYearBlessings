package com.NewYearBlessings.service.impl;

import com.NewYearBlessings.enums.ErrorType;
import com.NewYearBlessings.exception.ImageException;
import com.NewYearBlessings.entity.BlessingImage;
import com.NewYearBlessings.mapper.BlessingImageMapper;
import com.NewYearBlessings.service.ImageService;
import io.minio.*;
import io.minio.http.Method;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 图片服务 实现类
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private BlessingImageMapper blessingImageMapper;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${newyear-blessings.image.max-size}")
    private long maxImageSize;

    @Value("${newyear-blessings.image.allowed-formats}")
    private String allowedFormats;

    private final Set<String> ALLOWED_FORMAT_SET = new HashSet<>();

    // 初始化允许的图片格式
    @PostConstruct
    public void init() {
        if (allowedFormats != null) {
            ALLOWED_FORMAT_SET.addAll(Arrays.asList(allowedFormats.split(",")));
        }
    }

    @Override
    public String uploadImage(MultipartFile file, Long blessingId) throws Exception {
        // 验证图片
        Map<String, Object> validateResult = validateImage(file);
        if (!(boolean) validateResult.get("valid")) {
            throw new ImageException(ErrorType.IMAGE_UPLOAD_FAILED, (String) validateResult.get("message"));
        }

        // 生成随机文件名
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String randomFilename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        // 上传到MinIO
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(randomFilename)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        }

        // 保存图片信息到数据库
        BlessingImage blessingImage = new BlessingImage();
        blessingImage.setBlessingId(blessingId);
        blessingImage.setImageName(randomFilename);
        blessingImage.setOriginalName(originalFilename);
        blessingImage.setImageSize(file.getSize());
        blessingImage.setImageFormat(extension);
        blessingImage.setMinioBucket(bucketName);
        blessingImage.setMinioPath(randomFilename);
        blessingImage.setAccessCount(0);
        blessingImage.setIsDeleted(false);
        blessingImageMapper.insert(blessingImage);

        return randomFilename;
    }

    @Override
    public InputStream getImage(String imageName) throws Exception {
        // 从MinIO获取图片
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(imageName)
                        .build());
    }

    @Override
    public boolean deleteImage(String imageName) {
        try {
            // 从MinIO删除图片
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageName)
                            .build());

            // 更新数据库中图片状态
            BlessingImage blessingImage = blessingImageMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<BlessingImage>()
                            .eq("image_name", imageName));
            if (blessingImage != null) {
                blessingImage.setIsDeleted(true);
                blessingImageMapper.updateById(blessingImage);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getImageUrl(String imageName) {
        // 生成预签名URL，有效期1小时
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(imageName)
                            .expiry(3600, TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> validateImage(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        // 检查文件是否为空
        if (file.isEmpty()) {
            result.put("valid", false);
            result.put("message", "图片文件不能为空");
            return result;
        }

        // 检查文件大小
        if (file.getSize() > maxImageSize) {
            result.put("valid", false);
            result.put("message", String.format("图片大小不能超过%.2fMB", maxImageSize / (1024 * 1024)));
            return result;
        }

        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            result.put("valid", false);
            result.put("message", "图片文件格式无效");
            return result;
        }

        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        if (!ALLOWED_FORMAT_SET.contains(extension)) {
            result.put("valid", false);
            result.put("message", "只允许上传" + String.join(", ", ALLOWED_FORMAT_SET) + "格式的图片");
            return result;
        }

        // 验证文件内容（通过文件签名/魔数）
        try {
            byte[] fileBytes = file.getInputStream().readNBytes(8);
            String fileMagic = bytesToHex(fileBytes).toUpperCase();
            
            // 检查文件签名是否匹配
            if (extension.equals("jpg") || extension.equals("jpeg")) {
                if (!fileMagic.startsWith("FFD8FF")) {
                    result.put("valid", false);
                    result.put("message", "JPEG图片格式验证失败");
                    return result;
                }
            } else if (extension.equals("png")) {
                if (!fileMagic.startsWith("89504E470D0A1A0A")) {
                    result.put("valid", false);
                    result.put("message", "PNG图片格式验证失败");
                    return result;
                }
            } else if (extension.equals("webp")) {
                if (!fileMagic.startsWith("52494646") || !bytesToHex(Arrays.copyOfRange(fileBytes, 8, 12)).equals("57454250")) {
                    result.put("valid", false);
                    result.put("message", "WebP图片格式验证失败");
                    return result;
                }
            }
        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "图片内容验证失败");
            return result;
        }

        result.put("valid", true);
        return result;
    }

    /**
     * 将字节数组转换为十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
