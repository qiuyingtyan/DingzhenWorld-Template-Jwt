package com.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

// 使用Lombok注解@Data和@AllArgsConstructor，自动生成getter、setter和全参构造函数
@Data
@AllArgsConstructor
public class ConfirmResetVO {
    // 使用jakarta.validation.constraints.Email注解，验证email字段是否为合法的邮箱地址
    @Email
    String email;
    // 使用org.hibernate.validator.constraints.Length注解，验证code字段长度是否为6
    @Length(max = 6, min = 6)
    String code;
}
