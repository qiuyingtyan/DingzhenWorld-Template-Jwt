package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ConfirmResetVO;
import com.example.entity.vo.request.EmailRegisterVO;
import com.example.entity.vo.request.EmailResetVO;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

// 标识这是一个服务类
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    // 从配置文件中获取验证邮件的限制次数
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    // 注入AmqpTemplate，用于发送消息
    @Resource
    AmqpTemplate rabbitTemplate;

    // 注入StringRedisTemplate，用于操作Redis
    @Resource
    StringRedisTemplate stringRedisTemplate;

    // 注入PasswordEncoder，用于密码加密
    @Resource
    PasswordEncoder passwordEncoder;

    // 注入FlowUtils，用于限流
    @Resource
    FlowUtils flow;

    // 根据用户名或邮箱加载用户信息
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if(account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    // 发送注册邮件验证码
    public String registerEmailVerifyCode(String type, String email, String address){
        synchronized (address.intern()) {
            // 检查验证邮件的限制次数
            if(!this.verifyLimit(address))
                return "请求频繁，请稍后再试";
            // 生成随机验证码
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            // 构造消息内容
            Map<String, Object> data = Map.of("type",type,"email", email, "code", code);
            // 发送消息
            rabbitTemplate.convertAndSend(Const.MQ_MAIL, data);
            // 将验证码存入Redis，设置过期时间为3分钟
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    // 注册邮箱账号
    public String registerEmailAccount(EmailRegisterVO info){
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(info.getCode())) return "验证码错误，请重新输入";
        if(this.existsAccountByEmail(email)) return "该邮件地址已被注册";
        String username = info.getUsername();
        if(this.existsAccountByUsername(username)) return "该用户名已被他人使用，请重新更换";
        String password = passwordEncoder.encode(info.getPassword());
        Account account = new Account(null, info.getUsername(),
                password, email, Const.ROLE_DEFAULT, new Date());
        if(!this.save(account)) {
            return "内部错误，注册失败";
        } else {
            this.deleteEmailVerifyCode(email);
            return null;
        }
    }

    // 重置邮箱账号密码
    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if(verify != null) return verify;
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if(update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    // 验证重置密码的验证码
    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(info.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    // 删除邮箱验证码
    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    // 获取邮箱验证码
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    // 检查验证邮件的限制次数
    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flow.limitOnceCheck(key, verifyLimit);
    }

    // 根据用户名或邮箱查找账号
    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    // 根据邮箱判断账号是否存在
    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    // 根据用户名判断账号是否存在
    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }
}