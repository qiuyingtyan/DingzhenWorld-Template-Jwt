package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Supplier;

// 验证注解
@Validated
// 控制器注解
@RestController
// 请求映射注解
@RequestMapping("/api/auth")
public class AuthorizeController {

    // 资源注解
    @Resource
    AccountService accountService;

    // 获取验证码
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset)")  String type,
                                        HttpServletRequest request){
        // 调用服务层方法，获取验证码
        return this.messageHandle(() ->
                accountService.registerEmailVerifyCode(type, String.valueOf(email), request.getRemoteAddr()));
    }

    // 注册
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVO vo){
        // 调用服务层方法，注册账号
        return this.messageHandle(() ->
                accountService.registerEmailAccount(vo));
    }

    // 重置确认
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody @Valid ConfirmResetVO vo){
        // 调用服务层方法，确认重置
        return this.messageHandle(() -> accountService.resetConfirm(vo));
    }

    // 重置密码
    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO vo){
        // 调用服务层方法，重置密码
        return this.messageHandle(() ->
                accountService.resetEmailAccountPassword(vo));
    }

    // 处理消息
    private <T> RestBean<T> messageHandle(Supplier<String> action){
        // 获取消息
        String message = action.get();
        // 如果消息为空，返回成功
        if(message == null)
            return RestBean.success();
        // 否则返回失败
        else
            return RestBean.failure(400, message);
    }
}
