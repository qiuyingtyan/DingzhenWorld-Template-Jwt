package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@Order(Const.ORDER_FLOW_LIMIT)
public class FlowLimitingFilter extends HttpFilter {

    // 注入StringRedisTemplate
    @Resource
    StringRedisTemplate template;
    // 注入限流次数
    @Value("${spring.web.flow.limit}")
    int limit;
    // 注入限流周期
    @Value("${spring.web.flow.period}")
    int period;
    // 注入限流时间
    @Value("${spring.web.flow.block}")
    int block;

    // 注入FlowUtils
    @Resource
    FlowUtils utils;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 获取客户端IP地址
        String address = request.getRemoteAddr();
        // 尝试计数
        if (!tryCount(address))
            // 如果计数失败，则返回限流信息
            this.writeBlockMessage(response);
        else
            // 否则继续执行过滤器链
            chain.doFilter(request, response);
    }

    // 尝试计数
    private boolean tryCount(String address) {
        // 使用同步锁，防止多线程同时访问
        synchronized (address.intern()) {
            // 如果该IP地址已经被限流，则返回false
            if(Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + address)))
                return false;
            // 获取计数器key
            String counterKey = Const.FLOW_LIMIT_COUNTER + address;
            // 获取限流key
            String blockKey = Const.FLOW_LIMIT_BLOCK + address;
            // 调用utils.limitPeriodCheck方法进行限流判断
            return utils.limitPeriodCheck(counterKey, blockKey, block, limit, period);
        }
    }

    // 返回限流信息
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        // 设置响应状态码为403
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // 设置响应内容类型为json
        response.setContentType("application/json;charset=utf-8");
        // 获取响应输出流
        PrintWriter writer = response.getWriter();
        // 返回限流信息
        writer.write(RestBean.forbidden("操作频繁，请稍后再试").asJsonString());
    }
}
