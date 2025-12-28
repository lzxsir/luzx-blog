package com.luzx.config;

import com.luzx.filter.JwtAuthenticationFilter;
import com.luzx.service.impl.CustomUserDetailsService;
import com.luzx.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig
{
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;


    // 白名单列表
    private static final String[] WHITE_LIST = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh"
    };


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        /**
         * 1. 配置CORS
         * 2.关闭CSRF
         */
        http
        .cors().and() // 1.配置cors
        .csrf().disable() // 2.关闭csrf
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //  3. 配置会话管理（无状态），使用JWT
        .and()
        .authorizeHttpRequests(authz ->
                        authz.antMatchers(WHITE_LIST).permitAll() // 白名单 不需要认证
//                        .antMatchers("/api/admin/**").hasRole("ADMIN") // 在jwtAuthenticationFilter处理 需要特定角色的接口
                                .antMatchers("/api/**").authenticated() // 需要认证的接口
                            .anyRequest().authenticated() // 其他所有请求需认证
        )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // 插入自定义JWT过滤器
                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler())
                .authenticationEntryPoint(customAuthenticationEntryPoint())
        ;
        return http.build();
    }


    /**
     * 权限不足处理器（返回403）
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler()
    {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=utf-8");

            PrintWriter writer = response.getWriter();
            writer.write("{\n" +
                    "  \"code\": 403,\n" +
                    "  \"message\": \"权限不足，无法访问\",\n" +
                    "  \"data\": null,\n" +
                    "  \"success\": false\n" +
                    "}");
            writer.flush();
            writer.close();
        };
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint()
    {
        return  (request, response, authException) -> {
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.write("{\n" +
                    "  \"code\": 401,\n" +
                    "  \"message\": \"未认证，请先登录\",\n" +
                    "  \"data\": null,\n" +
                    "  \"success\": false\n" +
                    "}");
            writer.flush();
            writer.close();
        };
    }


}
