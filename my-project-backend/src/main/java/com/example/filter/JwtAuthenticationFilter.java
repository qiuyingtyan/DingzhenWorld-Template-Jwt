package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 组件注解，表示该类是一个Spring组件
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 资源注解，表示该类需要注入JwtUtils类
    @Resource
    JwtUtils utils;

    // 重写OncePerRequestFilter类的doFilterInternal方法
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Authorization字段
        String authorization = request.getHeader("Authorization");
        // 解析Authorization字段中的JWT
        DecodedJWT jwt = utils.resolveJwt(authorization);
        // 如果JWT不为空
        if(jwt != null) {
            // 将JWT转换为UserDetails对象
            UserDetails user = utils.toUser(jwt);
            // 创建UsernamePasswordAuthenticationToken对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // 设置认证详情
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证信息放入SecurityContextHolder中
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 将用户ID放入请求属性中
            request.setAttribute(Const.ATTR_USER_ID, utils.toId(jwt));
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
