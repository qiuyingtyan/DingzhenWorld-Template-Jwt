package com.example.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.MDC;

import java.util.Optional;

// 定义一个泛型类RestBean，用于封装返回结果
public record RestBean<T> (long id, int code, T data, String message) {
    // 成功返回结果，data为空
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(requestId(), 200, data, "请求成功");
    }

    // 成功返回结果，data为空
    public static <T> RestBean<T> success(){
        return success(null);
    }

    // 返回403错误，message为错误信息
    public static <T> RestBean<T> forbidden(String message){
        return failure(403, message);
    }

    // 返回401错误，message为错误信息
    public static <T> RestBean<T> unauthorized(String message){
        return failure(401, message);
    }

    // 返回指定错误码和错误信息
    public static <T> RestBean<T> failure(int code, String message){
        return new RestBean<>(requestId(), code, null, message);
    }

    // 将RestBean对象转换为JSON字符串
    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }

    // 获取请求ID
    private static long requestId(){
        String requestId = Optional.ofNullable(MDC.get("reqId")).orElse("0");
        return Long.parseLong(requestId);
    }
}
