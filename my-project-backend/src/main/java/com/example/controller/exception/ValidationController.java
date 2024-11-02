package com.example.controller.exception;

import com.example.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationController {

    // 捕获ValidationException异常
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validateError(ValidationException exception) {
        // 记录警告日志
        log.warn("Resolved [{}: {}]", exception.getClass().getName(), exception.getMessage());
        // 返回错误信息
        return RestBean.failure(400, "请求参数有误");
    }
}