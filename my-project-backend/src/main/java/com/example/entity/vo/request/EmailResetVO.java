package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailResetVO {
    // 邮箱
    @Email
    String email;
    // 验证码，长度为6
    @Length(max = 6, min = 6)
    String code;
    // 密码，长度为6-20
    @Length(min = 6, max = 20)
    String password;
}