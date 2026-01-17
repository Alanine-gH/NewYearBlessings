package com.NewYearBlessings.exception;

import com.NewYearBlessings.enums.ErrorType;

/**
 * 系统异常类
 * 用于表示系统级别的错误
 */
public class SystemException extends BaseException {
    
    public SystemException(ErrorType errorType) {
        super(errorType);
    }
    
    public SystemException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
    
    public SystemException(ErrorType errorType, Throwable cause) {
        super(errorType, cause);
    }
    
    public SystemException(ErrorType errorType, String customMessage, Throwable cause) {
        super(errorType, customMessage, cause);
    }
    
    public SystemException(int code, String message) {
        super(code, message);
    }
    
    public SystemException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}