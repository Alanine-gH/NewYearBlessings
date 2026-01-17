package com.NewYearBlessings.exception;

import com.NewYearBlessings.enums.ErrorType;

/**
 * 图片相关异常类
 * 用于表示图片上传、获取等过程中出现的错误
 */
public class ImageException extends BusinessException {
    
    public ImageException(ErrorType errorType) {
        super(errorType);
    }
    
    public ImageException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
    
    public ImageException(ErrorType errorType, Throwable cause) {
        super(errorType, cause);
    }
    
    public ImageException(ErrorType errorType, String customMessage, Throwable cause) {
        super(errorType, customMessage, cause);
    }
    
    public ImageException(int code, String message) {
        super(code, message);
    }
    
    public ImageException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}