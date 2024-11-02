package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class EmailRegisterVO {
    // 邮箱
    @Email
    String email;
    // 验证码，长度为6
    @Length(max = 6, min = 6)
    String code;
    // 用户名，只能包含字母、数字和中文，长度为1-10
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    String username;
    // 密码，长度为6-20
    @Length(min = 6, max = 20)
    String password;
}
