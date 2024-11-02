package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthenticationFilter;
import com.example.filter.RequestLogFilter;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    // 注入JwtAuthenticationFilter
    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    // 注入RequestLogFilter
    @Resource
    RequestLogFilter requestLogFilter;

    // 注入JwtUtils
    @Resource
    JwtUtils utils;

    // 注入AccountService
    @Resource
    AccountService service;

    // 配置SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 配置请求授权
                .authorizeHttpRequests(conf -> conf
                        // 允许所有对/api/auth/**和/error的访问
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        // 允许所有对/swagger-ui/**和/v3/api-docs/**的访问
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 其他请求需要具有Const.ROLE_DEFAULT角色
                        .anyRequest().hasAnyRole(Const.ROLE_DEFAULT)
                )
                // 配置表单登录
                .formLogin(conf -> conf
                        // 登录处理URL
                        .loginProcessingUrl("/api/auth/login")
                        // 登录失败处理
                        .failureHandler(this::handleProcess)
                        // 登录成功处理
                        .successHandler(this::handleProcess)
                        // 允许所有对登录的访问
                        .permitAll()
                )
                // 配置注销
                .logout(conf -> conf
                        // 注销URL
                        .logoutUrl("/api/auth/logout")
                        // 注销成功处理
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                // 配置异常处理
                .exceptionHandling(conf -> conf
                        // 访问被拒绝处理
                        .accessDeniedHandler(this::handleProcess)
                        // 认证入口点处理
                        .authenticationEntryPoint(this::handleProcess)
                )
                // 禁用CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理
                .sessionManagement(conf -> conf
                        // 无状态会话
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 在UsernamePasswordAuthenticationFilter之前添加RequestLogFilter
                .addFilterBefore(requestLogFilter, UsernamePasswordAuthenticationFilter.class)
                // 在RequestLogFilter之前添加JwtAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, RequestLogFilter.class)
                // 构建SecurityFilterChain
                .build();
    }

    // 处理异常、认证和注销
    private void handleProcess(HttpServletRequest request,
                               HttpServletResponse response,
                               Object exceptionOrAuthentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        if(exceptionOrAuthentication instanceof AccessDeniedException exception) {
            // 访问被拒绝
            writer.write(RestBean
                    .forbidden(exception.getMessage()).asJsonString());
        } else if(exceptionOrAuthentication instanceof Exception exception) {
            // 未授权
            writer.write(RestBean
                    .unauthorized(exception.getMessage()).asJsonString());
        } else if(exceptionOrAuthentication instanceof Authentication authentication){
            // 认证成功
            User user = (User) authentication.getPrincipal();
            Account account = service.findAccountByNameOrEmail(user.getUsername());
            String jwt = utils.createJwt(user, account.getUsername(), account.getId());
            if(jwt == null) {
                // 登录验证频繁
                writer.write(RestBean.forbidden("登录验证频繁，请稍后再试").asJsonString());
            } else {
                // 返回授权信息
                AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, o -> o.setToken(jwt));
                vo.setExpire(utils.expireTime());
                writer.write(RestBean.success(vo).asJsonString());
            }
        }
    }

    // 注销成功处理
    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if(utils.invalidateJwt(authorization)) {
            // 注销成功
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }
        // 注销失败
        writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
    }
}
