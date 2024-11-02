package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

// 定义一个AccountService接口，继承IService和UserDetailsService接口
public interface AccountService extends IService<Account>, UserDetailsService {
    // 根据用户名或邮箱查找账户
    Account findAccountByNameOrEmail(String text);
    // 注册邮箱验证码
    String registerEmailVerifyCode(String type, String email, String address);
    // 注册邮箱账户
    String registerEmailAccount(EmailRegisterVO info);
    // 重置邮箱账户密码
    String resetEmailAccountPassword(EmailResetVO info);
    // 重置确认
    String resetConfirm(ConfirmResetVO info);
}
