package com.luzx.service;

import com.luzx.model.User;
import com.luzx.repository.UserRepository;
import com.luzx.service.impl.CustomUserDetailsService;
import com.luzx.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService
{

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 用户登录认证
    public Map<String, String> login(String username, String password)
    {
        try
        {
            // 认证用户
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 生成双Token
            String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            tokens.put("tokenType", "Bearer");
            tokens.put("expiresIn", String.valueOf(jwtTokenUtil.getAccessTokenExpiration() / 1000));

             log.info("用户登录成功: {}", username);
            return tokens;

        } catch (BadCredentialsException e) {
            log.error("用户登录失败: {}", username, e);
            throw new RuntimeException("用户名或密码错误");
        }
    }

    // 刷新Access Token
    public Map<String, String> refreshToken(String refreshToken) {
        try {
            // 验证Refresh Token
            if (jwtTokenUtil.isTokenExpired(refreshToken)) {
                throw new RuntimeException("Refresh Token已过期");
            }

            String tokenType = jwtTokenUtil.getTokenType(refreshToken);
            if (!"REFRESH_TOKEN".equals(tokenType)) {
                throw new RuntimeException("无效的Token类型");
            }

            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtTokenUtil.validateToken(refreshToken, userDetails)) {
                throw new RuntimeException("Refresh Token验证失败");
            }

            // 生成新的Access Token
            String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("tokenType", "Bearer");
            tokens.put("expiresIn", String.valueOf(jwtTokenUtil.getAccessTokenExpiration() / 1000));

            log.info("Token刷新成功: {}", username);
            return tokens;

        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            throw new RuntimeException("Token刷新失败: " + e.getMessage());
        }
    }

    // 用户注册
    @Transactional
    public User register(String username, String password)
    {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setLoginName(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedTime(LocalDateTime.now());
        user.setRoles(Collections.singletonList("USER"));
        userRepository.save(user);
        return user;
    }

    // 获取当前用户信息
    public Optional<User> getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }

}
