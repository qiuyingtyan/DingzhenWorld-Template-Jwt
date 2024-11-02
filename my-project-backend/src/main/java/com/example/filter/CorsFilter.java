package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 定义一个CorsFilter类，继承自HttpFilter
@Component
// 设置过滤器的执行顺序
@Order(Const.ORDER_CORS)
public class CorsFilter extends HttpFilter {

    // 注入配置文件中的origin属性
    @Value("${spring.web.cors.origin}")
    String origin;

    // 注入配置文件中的credentials属性
    @Value("${spring.web.cors.credentials}")
    boolean credentials;

    // 注入配置文件中的methods属性
    @Value("${spring.web.cors.methods}")
    String methods;

    // 重写doFilter方法，用于处理请求和响应
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 添加CORS响应头
        this.addCorsHeader(request, response);
        // 继续执行过滤器链
        chain.doFilter(request, response);
    }

    // 添加CORS响应头
    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        // 设置Access-Control-Allow-Origin响应头
        response.addHeader("Access-Control-Allow-Origin", this.resolveOrigin(request));
        // 设置Access-Control-Allow-Methods响应头
        response.addHeader("Access-Control-Allow-Methods", this.resolveMethod());
        // 设置Access-Control-Allow-Headers响应头
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        // 如果credentials为true，则设置Access-Control-Allow-Credentials响应头
        if(credentials) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
    }

    // 解析methods属性
    private String resolveMethod(){
        // 如果methods属性为"*"，则返回所有HTTP方法
        return methods.equals("*") ? "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH" : methods;
    }

    // 解析origin属性
    private String resolveOrigin(HttpServletRequest request){
        // 如果origin属性为"*"，则返回请求头中的Origin
        return origin.equals("*") ? request.getHeader("Origin") : origin;
    }
}
