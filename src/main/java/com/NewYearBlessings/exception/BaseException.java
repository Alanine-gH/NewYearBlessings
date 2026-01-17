package com.NewYearBlessings.exception;

import com.NewYearBlessings.enums.ErrorType;
import lombok.Getter;

/**
 * 基础自定义异常类
 * 所有业务异常都应继承此类
 */
@Getter
public class BaseException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final int code;
    
    /**
     * 错误信息
     */
    private final String message;
    
    /**
     * 通过ErrorType构造异常
     * @param errorType 错误类型枚举
     */
    public BaseException(ErrorType errorType) {
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }
    
    /**
     * 通过ErrorType和自定义消息构造异常
     * @param errorType 错误类型枚举
     * @param customMessage 自定义错误消息
     */
    public BaseException(ErrorType errorType, String customMessage) {
        this.code = errorType.getCode();
        this.message = customMessage;
    }
    
    /**
     * 直接通过错误码和错误信息构造异常
     * @param code 错误码
     * @param message 错误信息
     */
    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    /**
     * 带原始异常的构造方法
     * @param errorType 错误类型枚举
     * @param cause 原始异常
     */
    public BaseException(ErrorType errorType, Throwable cause) {
        super(cause);
        this.code = errorType.getCode();
        this.message = errorType.getMessage();
    }
    
    /**
     * 带自定义消息和原始异常的构造方法
     * @param errorType 错误类型枚举
     * @param customMessage 自定义错误消息
     * @param cause 原始异常
     */
    public BaseException(ErrorType errorType, String customMessage, Throwable cause) {
        super(cause);
        this.code = errorType.getCode();
        this.message = customMessage;
    }
    
    /**
     * 直接通过错误码、错误信息和原始异常构造
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原始异常
     */
    public BaseException(int code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
    }
}