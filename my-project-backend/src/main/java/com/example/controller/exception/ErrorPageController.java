package com.example.controller.exception;

import com.example.entity.RestBean;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class ErrorPageController extends AbstractErrorController {

    // 构造函数，传入ErrorAttributes对象
    public ErrorPageController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    // 处理错误请求
    @RequestMapping
    public RestBean<Void> error(HttpServletRequest request) {
        // 获取错误状态码
        HttpStatus status = this.getStatus(request);
        // 获取错误信息
        Map<String, Object> errorAttributes = this.getErrorAttributes(request, this.getAttributeOptions());
        // 转换错误信息
        String message = this.convertErrorMessage(status)
                .orElse(errorAttributes.get("message").toString());
        // 返回错误信息
        return RestBean.failure(status.value(), message);
    }

    // 根据错误状态码转换错误信息
    private Optional<String> convertErrorMessage(HttpStatus status){
        String value = switch (status.value()) {
            case 400 -> "请求参数有误";
            case 404 -> "请求的接口不存在";
            case 405 -> "请求方法错误";
            case 500 -> "内部错误，请联系管理员";
            default -> null;
        };
        return Optional.ofNullable(value);
    }

    // 获取错误属性选项
    private ErrorAttributeOptions getAttributeOptions(){
        return ErrorAttributeOptions
                .defaults()
                .including(ErrorAttributeOptions.Include.MESSAGE,
                        ErrorAttributeOptions.Include.EXCEPTION);
    }
}
