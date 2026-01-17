package com.NewYearBlessings.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 祝福提交DTO
 */
@Data
public class BlessingSubmitDTO {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    @NotBlank(message = "祝福内容不能为空")
    @Size(min = 1, max = 200, message = "祝福内容长度必须在1-200个字符之间")
    private String blessingContent;

    @Size(max = 50, message = "城市名称长度不能超过50个字符")
    private String city;

    private String imageName; // 图片名称
}