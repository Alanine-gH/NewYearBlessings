package com.NewYearBlessings.exception;

import com.NewYearBlessings.enums.ErrorType;

/**
 * 业务异常类
 * 用于表示业务逻辑层面的错误
 */
public class BusinessException extends BaseException {
    
    public BusinessException(ErrorType errorType) {
        super(errorType);
    }
    
    public BusinessException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
    
    public BusinessException(ErrorType errorType, Throwable cause) {
        super(errorType, cause);
    }
    
    public BusinessException(ErrorType errorType, String customMessage, Throwable cause) {
        super(errorType, customMessage, cause);
    }
    
    public BusinessException(int code, String message) {
        super(code, message);
    }
    
    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}