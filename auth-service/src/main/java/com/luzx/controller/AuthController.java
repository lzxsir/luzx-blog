package com.luzx.controller;

import com.luzx.model.User;
import com.luzx.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final AuthService authService;

//    @PreAuthorize(value = "1")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request)
    {
        try
        {
            Map<String, String> tokens = authService.login(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse(200, "登录成功", tokens));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request)
    {
        try
        {
            Map<String, String> tokens = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(new ApiResponse(200, "Token刷新成功", tokens));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse(200, "注册成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(400, e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser()
    {
        Optional<User> user = authService.getCurrentUser();
        return user.map(value -> ResponseEntity.ok(new ApiResponse(200, "获取成功", value))).orElseGet(() -> ResponseEntity.badRequest().body(new ApiResponse(401, "未授权", null)));
    }

    // 请求参数类
    @Data
    public static class LoginRequest
    {
        private String username;
        private String password;
    }

    @Data
    public static class RefreshTokenRequest {
        private String refreshToken;
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class ApiResponse {
        private int code;
        private String message;
        private Object data;
    }

}
