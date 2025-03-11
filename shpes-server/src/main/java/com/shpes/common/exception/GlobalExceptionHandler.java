package com.shpes.common.exception;

import com.shpes.common.api.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义API异常
     */
    @ExceptionHandler(ApiException.class)
    public CommonResult<Void> handleApiException(ApiException e) {
        if (e.getErrorCode() != null) {
            LOGGER.error("API异常: {}", e.getMessage());
            return CommonResult.failed(e.getErrorCode());
        }
        LOGGER.error("API异常: {}", e.getMessage());
        return CommonResult.failed(e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<Void> handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        LOGGER.error("参数验证异常: {}", message);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public CommonResult<Void> handleBindException(BindException e) {
        String message = null;
        if (e.getBindingResult().hasErrors()) {
            FieldError fieldError = e.getBindingResult().getFieldError();
            if (fieldError != null) {
                message = fieldError.getField() + fieldError.getDefaultMessage();
            }
        }
        LOGGER.error("参数绑定异常: {}", message);
        return CommonResult.validateFailed(message);
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public CommonResult<Void> handleBadCredentialsException(BadCredentialsException e) {
        LOGGER.error("认证异常: {}", e.getMessage());
        return CommonResult.unauthorized("用户名或密码错误");
    }

    /**
     * 处理用户不存在异常
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public CommonResult<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        LOGGER.error("用户不存在: {}", e.getMessage());
        return CommonResult.unauthorized(e.getMessage());
    }

    /**
     * 处理访问权限异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult<Void> handleAccessDeniedException(AccessDeniedException e) {
        LOGGER.error("访问权限异常: {}", e.getMessage());
        return CommonResult.forbidden("没有相关权限");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public CommonResult<Void> handleException(Exception e) {
        LOGGER.error("系统异常: ", e);
        return CommonResult.failed("系统异常，请联系管理员");
    }
} 