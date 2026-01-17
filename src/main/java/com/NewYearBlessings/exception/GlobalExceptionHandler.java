package com.NewYearBlessings.exception;

import com.NewYearBlessings.common.R;
import com.NewYearBlessings.enums.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有控制器层抛出的异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理自定义基础异常
     * @param e 基础异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<R<Object>> handleBaseException(BaseException e) {
        log.error("BaseException occurred: [code: {}, message: {}]", e.getCode(), e.getMessage(), e);
        R<Object> result = new R<>();
        result.setCode(e.getCode());
        result.setMsg(e.getMessage());
        // 根据错误码设置HTTP状态码
        HttpStatus httpStatus = getHttpStatusByCode(e.getCode());
        return new ResponseEntity<>(result, httpStatus);
    }
    
    /**
     * 处理方法参数验证异常
     * @param e 参数验证异常
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("MethodArgumentNotValidException occurred: {}", errorMessage, e);
        
        R<Object> result = new R<>();
        result.setCode(ErrorType.PARAM_VALIDATION_ERROR.getCode());
        result.setMsg(errorMessage);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 处理绑定异常
     * @param e 绑定异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Object>> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMessage = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("BindException occurred: {}", errorMessage, e);
        
        R<Object> result = new R<>();
        result.setCode(ErrorType.PARAM_VALIDATION_ERROR.getCode());
        result.setMsg(errorMessage);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 处理404异常
     * @param e 404异常
     * @return 统一响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<R<Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error("NoHandlerFoundException occurred: {}", e.getMessage(), e);
        
        R<Object> result = new R<>();
        result.setCode(ErrorType.RESOURCE_NOT_FOUND.getCode());
        result.setMsg("请求的资源不存在");
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
    
    /**
     * 处理客户端连接断开异常
     * @param e 客户端连接断开异常
     * @return 统一响应结果
     */
    @ExceptionHandler(org.springframework.web.context.request.async.AsyncRequestNotUsableException.class)
    public ResponseEntity<R<Object>> handleAsyncRequestNotUsableException(org.springframework.web.context.request.async.AsyncRequestNotUsableException e) {
        // 客户端连接已断开，无需记录为错误日志
        log.debug("Client connection disconnected: {}", e.getMessage());
        
        R<Object> result = new R<>();
        result.setCode(ErrorType.SYSTEM_ERROR.getCode());
        result.setMsg(ErrorType.SYSTEM_ERROR.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK); // 返回OK，因为这是客户端问题
    }
    
    /**
     * 处理所有未捕获的异常
     * @param e 未捕获的异常
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<R<Object>> handleException(Exception e) {
        // 特殊处理客户端断开连接的IOException
        if (e.getCause() instanceof java.io.IOException && 
            e.getCause().getMessage() != null && 
            e.getCause().getMessage().contains("主机中的软件中止了一个已建立的连接")) {
            log.debug("Client connection aborted: {}", e.getMessage());
            return new ResponseEntity<>(new R<>(), HttpStatus.OK);
        }
        
        log.error("Unexpected Exception occurred: {}", e.getMessage(), e);
        
        R<Object> result = new R<>();
        result.setCode(ErrorType.SYSTEM_ERROR.getCode());
        result.setMsg(ErrorType.SYSTEM_ERROR.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 根据错误码获取对应的HTTP状态码
     * @param code 错误码
     * @return HTTP状态码
     */
    private HttpStatus getHttpStatusByCode(int code) {
        // 系统级错误 (10000-19999) - 通常返回500
        if (code >= 10000 && code < 20000) {
            // 资源不存在返回404
            if (code == ErrorType.RESOURCE_NOT_FOUND.getCode()) {
                return HttpStatus.NOT_FOUND;
            }
            // 参数验证失败返回400
            if (code == ErrorType.PARAM_VALIDATION_ERROR.getCode()) {
                return HttpStatus.BAD_REQUEST;
            }
            // 请求频率过高返回429
            if (code == ErrorType.RATE_LIMIT_EXCEEDED.getCode()) {
                return HttpStatus.TOO_MANY_REQUESTS;
            }
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        // 业务级错误 (20000-29999) - 通常返回400
        if (code >= 20000 && code < 30000) {
            // 资源不存在返回404
            if (code == ErrorType.BLESSING_NOT_FOUND.getCode() || code == ErrorType.CITY_NOT_FOUND.getCode()) {
                return HttpStatus.NOT_FOUND;
            }
            return HttpStatus.BAD_REQUEST;
        }
        // 第三方服务错误 (30000-39999) - 返回502
        if (code >= 30000 && code < 40000) {
            return HttpStatus.BAD_GATEWAY;
        }
        // 认证授权错误 (40000-49999) - 返回401或403
        if (code >= 40000 && code < 50000) {
            if (code == ErrorType.UNAUTHORIZED_ACCESS.getCode() || code == ErrorType.TOKEN_EXPIRED.getCode() || code == ErrorType.TOKEN_INVALID.getCode()) {
                return HttpStatus.UNAUTHORIZED;
            }
            if (code == ErrorType.PERMISSION_DENIED.getCode()) {
                return HttpStatus.FORBIDDEN;
            }
        }
        // 默认返回500
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}