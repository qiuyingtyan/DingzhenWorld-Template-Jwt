package com.example.filter;

import com.alibaba.fastjson2.JSONObject;
import com.example.utils.Const;
import com.example.utils.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class RequestLogFilter extends OncePerRequestFilter {

    // 注入SnowflakeIdGenerator
    @Resource
    SnowflakeIdGenerator generator;

    // 忽略的URL集合
    private final Set<String> ignores = Set.of("/swagger-ui", "/v3/api-docs");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 判断是否忽略该URL
        if(this.isIgnoreUrl(request.getServletPath())) {
            // 如果忽略，则直接放行
            filterChain.doFilter(request, response);
        } else {
            // 记录请求开始时间
            long startTime = System.currentTimeMillis();
            // 记录请求开始日志
            this.logRequestStart(request);
            // 包装响应
            ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
            // 放行请求
            filterChain.doFilter(request, wrapper);
            // 记录请求结束日志
            this.logRequestEnd(wrapper, startTime);
            // 将响应内容复制到原始响应中
            wrapper.copyBodyToResponse();
        }
    }

    // 判断是否忽略该URL
    private boolean isIgnoreUrl(String url){
        for (String ignore : ignores) {
            if(url.startsWith(ignore)) return true;
        }
        return false;
    }

    // 记录请求结束日志
    public void logRequestEnd(ContentCachingResponseWrapper wrapper, long startTime){
        // 计算请求耗时
        long time = System.currentTimeMillis() - startTime;
        // 获取响应状态码
        int status = wrapper.getStatus();
        // 获取响应内容
        String content = status != 200 ?
                status + " 错误" : new String(wrapper.getContentAsByteArray());
        // 记录日志
        log.info("请求处理耗时: {}ms | 响应结果: {}", time, content);
    }

    // 记录请求开始日志
    public void logRequestStart(HttpServletRequest request){
        // 生成请求ID
        long reqId = generator.nextId();
        // 将请求ID放入MDC
        MDC.put("reqId", String.valueOf(reqId));
        // 创建JSON对象，用于存储请求参数
        JSONObject object = new JSONObject();
        // 将请求参数放入JSON对象
        request.getParameterMap().forEach((k, v) -> object.put(k, v.length > 0 ? v[0] : null));
        // 获取用户ID
        Object id = request.getAttribute(Const.ATTR_USER_ID);
        // 如果用户ID不为空
        if(id != null) {
            // 获取用户信息
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // 记录日志
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} │ 身份: {} (UID: {}) | 角色: {} | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(),
                    user.getUsername(), id, user.getAuthorities(), object);
        } else {
            // 记录日志
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} │ 身份: 未验证 | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(), object);
        }
    }
}
