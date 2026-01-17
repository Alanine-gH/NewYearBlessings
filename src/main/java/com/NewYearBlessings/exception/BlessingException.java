package com.NewYearBlessings.exception;

import com.NewYearBlessings.enums.ErrorType;

/**
 * 祝福相关异常类
 * 用于表示祝福提交、查询等过程中出现的错误
 */
public class BlessingException extends BusinessException {
    
    public BlessingException(ErrorType errorType) {
        super(errorType);
    }
    
    public BlessingException(ErrorType errorType, String customMessage) {
        super(errorType, customMessage);
    }
    
    public BlessingException(ErrorType errorType, Throwable cause) {
        super(errorType, cause);
    }
    
    public BlessingException(ErrorType errorType, String customMessage, Throwable cause) {
        super(errorType, customMessage, cause);
    }
    
    public BlessingException(int code, String message) {
        super(code, message);
    }
    
    public BlessingException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}