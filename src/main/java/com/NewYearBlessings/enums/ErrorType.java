package com.NewYearBlessings.enums;

import lombok.Getter;

/**
 * 错误码枚举类
 * 统一管理系统中的错误码和错误信息
 */
@Getter
public enum ErrorType {
    // 系统级错误 10000-19999
    SYSTEM_ERROR(10000, "系统内部错误"),
    DATABASE_ERROR(10001, "数据库操作错误"),
    NETWORK_ERROR(10002, "网络连接错误"),
    PARAM_VALIDATION_ERROR(10003, "参数验证失败"),
    RESOURCE_NOT_FOUND(10004, "资源不存在"),
    FILE_UPLOAD_ERROR(10005, "文件上传失败"),
    RATE_LIMIT_EXCEEDED(10006, "请求频率过高，请稍后再试"),
    
    // 业务级错误 20000-29999
    BLESSING_SUBMIT_FAILED(20000, "祝福提交失败"),
    BLESSING_CONTENT_EMPTY(20001, "祝福内容不能为空"),
    BLESSING_CONTENT_TOO_LONG(20002, "祝福内容过长"),
    CITY_NOT_FOUND(20003, "城市信息不存在"),
    USER_IP_BLACKLISTED(20004, "您的IP已被限制访问"),
    IMAGE_UPLOAD_FAILED(20005, "图片上传失败"),
    IMAGE_SIZE_EXCEEDED(20006, "图片大小超过限制"),
    IMAGE_FORMAT_INVALID(20007, "图片格式无效"),
    BLESSING_NOT_FOUND(20008, "祝福信息不存在"),
    OPERATION_NOT_PERMITTED(20009, "操作不被允许"),
    
    // 第三方服务错误 30000-39999
    THIRD_PARTY_SERVICE_ERROR(30000, "第三方服务错误"),
    AI_SERVICE_ERROR(30001, "AI服务调用失败"),
    STORAGE_SERVICE_ERROR(30002, "存储服务错误"),
    
    // 认证授权错误 40000-49999
    UNAUTHORIZED_ACCESS(40000, "未授权访问"),
    TOKEN_EXPIRED(40001, "令牌已过期"),
    TOKEN_INVALID(40002, "令牌无效"),
    PERMISSION_DENIED(40003, "权限不足");
    
    private final int code;
    private final String message;
    
    ErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }
}