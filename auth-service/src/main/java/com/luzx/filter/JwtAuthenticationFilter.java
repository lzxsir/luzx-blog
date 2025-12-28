package com.luzx.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luzx.enums.JwtEnums;
import com.luzx.service.impl.CustomUserDetailsService;
import com.luzx.utils.JwtTokenUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter
{

    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private CustomUserDetailsService userDetailsService;
    @Resource
    private ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        try
        {
            String jwt = getTokenFromRequest(request);
            if (jwt != null && jwtTokenUtil.validateToken(jwt, getCurrentUserDetails(jwt))) {
                String tokenType = jwtTokenUtil.getTokenType(jwt);

                // 只处理ACCESS_TOKEN类型的Token
                if (JwtEnums.ACCESS_TOKEN.name().equals(tokenType))
                {
                    String username = jwtTokenUtil.getUsernameFromToken(jwt);

                    // 接口权限验证
//                    boolean hasPermission = permissionService.hasPermission(userId, requestURI, method);
//                    if (!hasPermission) {
//                        log.warn("权限拒绝: 用户[{}] 尝试访问 {} {}", username, method, requestURI);
//                        sendError(response, HttpServletResponse.SC_FORBIDDEN,
//                                String.format("权限不足，无法访问 %s %s", method, requestURI));
//                        return;
//                    }


                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("设置认证用户: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("无法设置用户认证: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }


    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. 从Header获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 2. 从参数获取
        String token = request.getParameter("token");
        if (StringUtils.hasText(token)) {
            return token;
        }

        return null;
    }


    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");

        Map<String, Object> result = new HashMap<>();
        result.put("code", status);
        result.put("message", message);
        result.put("data", null);
        result.put("success", false);
        result.put("timestamp", System.currentTimeMillis());

        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }

    private UserDetails getCurrentUserDetails(String token)
    {
        String username = jwtTokenUtil.getUsernameFromToken(token);
        return userDetailsService.loadUserByUsername(username);
    }
}
