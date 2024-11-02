package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    // 注入JavaMailSender
    @Resource
    JavaMailSender sender;

    // 注入邮件发送者
    @Value("${spring.mail.username}")
    String username;

    // 监听队列，处理消息
    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        // 获取邮件地址
        String email = data.get("email").toString();
        // 获取验证码
        Integer code = (Integer) data.get("code");
        // 根据消息类型创建邮件消息
        SimpleMailMessage message = switch (data.get("type").toString()) {
            case "register" ->
                    createMessage("欢迎注册顶针的世界",
                            "您的邮件注册验证码为: "+code+"，有效时间3分钟，为了保障你的马，请向他人泄露验证码信息。",
                            email);
            case "reset" ->
                    createMessage("您的密码重置邮件",
                            "你好，您正在执行重置密码操作，验证码: "+code+"，有效时间3分钟，如非本人操作，请无视。",
                            email);
            default -> null;
        };
        // 如果消息为空，则返回
        if(message == null) return;
        // 发送邮件
        sender.send(message);
    }

    // 创建邮件消息
    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
