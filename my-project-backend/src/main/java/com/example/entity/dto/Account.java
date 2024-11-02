package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

// 使用Lombok注解@Data，自动生成getter和setter方法
@Data
// 使用MyBatis Plus注解@TableName，指定数据库表名
@TableName("db_account")
// 使用Lombok注解@AllArgsConstructor，自动生成全参构造方法
@AllArgsConstructor
public class Account implements BaseData {
    // 使用MyBatis Plus注解@TableId，指定主键类型
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
    String email;
    String role;
    Date registerTime;
}