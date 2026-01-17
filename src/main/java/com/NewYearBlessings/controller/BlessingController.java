package com.NewYearBlessings.controller;

import com.NewYearBlessings.common.R;
import com.NewYearBlessings.service.UserBlessingService;
import com.NewYearBlessings.service.ImageService;
import com.NewYearBlessings.dto.BlessingSubmitDTO;
import com.NewYearBlessings.vo.BlessingVO;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.InputStream;
import java.util.List;

/**
 * 祝福信息控制器
 */
@RestController
@RequestMapping("/blessing")
@Tag(name = "祝福信息", description = "祝福相关接口")
@Slf4j
public class BlessingController {

    @Autowired
    private UserBlessingService userBlessingService;

    @Autowired
    private ImageService imageService;

    /**
     * 提交祝福
     */
    @PostMapping("/submit")
    @Operation(summary = "提交祝福")
    public R<String> submitBlessing(@Valid @RequestBody BlessingSubmitDTO submitDTO,
                                    @RequestHeader(value = "X-Real-IP", required = false) String ip,
                                    jakarta.servlet.http.HttpServletRequest request) {
        // 如果没有X-Real-IP头，则从request中获取客户端IP
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        userBlessingService.submitBlessing(submitDTO, ip);
        return R.success("祝福提交成功");
    }

    /**
     * 获取祝福列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取祝福列表")
    public R<List<BlessingVO>> getBlessings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 限制每页最大数据量，防止返回过多数据导致连接中断
            int maxSize = 50;
            if (size > maxSize) {
                size = maxSize;
            }

            List<BlessingVO> blessings = userBlessingService.getBlessings(page, size);
            return R.success(blessings);
        } catch (Exception e) {
            log.error("获取祝福列表失败: {}", e.getMessage(), e);
            return R.error("获取祝福列表失败");
        }
    }

    /**
     * 获取祝福总数
     */
    @GetMapping("/count")
    @Operation(summary = "获取祝福总数")
    public R<Long> getBlessingCount() {
        long count = userBlessingService.getBlessingCount();
        return R.success(count);
    }

    /**
     * 上传祝福图片
     */
    @PostMapping("/upload-image/{blessingId}")
    @Operation(summary = "上传祝福图片")
    public R<String> uploadImage(@PathVariable Long blessingId, @RequestParam("file") MultipartFile file) {
        try {
            String imageName = imageService.uploadImage(file, blessingId);
            return R.success(imageName);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取图片
     */
    @GetMapping("/image/{imageName}")
    @Operation(summary = "获取图片")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        try {
            InputStream inputStream = imageService.getImage(imageName);
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();
            return ResponseEntity.ok().body(bytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * 获取各城市祝福统计数据
     */
    @GetMapping("/city-stats")
    @Operation(summary = "获取各城市祝福统计数据")
    public R<List<java.util.Map<String, Object>>> getCityStats() {
        try {
            List<java.util.Map<String, Object>> stats = userBlessingService.getCityStats();
            return R.success(stats);
        } catch (Exception e) {
            log.error("获取城市统计数据失败: {}", e.getMessage(), e);
            return R.error("获取城市统计数据失败");
        }
    }

    /**
     * 获取指定城市的祝福列表
     */
    @GetMapping("/city/{city}")
    @Operation(summary = "获取指定城市的祝福列表")
    public R<List<BlessingVO>> getCityBlessings(
            @PathVariable String city,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            int maxSize = 50;
            if (size > maxSize) {
                size = maxSize;
            }
            List<BlessingVO> blessings = userBlessingService.getCityBlessings(city, page, size);
            return R.success(blessings);
        } catch (Exception e) {
            log.error("获取城市祝福列表失败: {}", e.getMessage(), e);
            return R.error("获取城市祝福列表失败");
        }
    }
}
