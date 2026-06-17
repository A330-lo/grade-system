package com.scs.interceptor;

import com.scs.annotation.RequireAuth;
import com.scs.exception.BusinessException;
import com.scs.utils.JwtUtils;
import com.scs.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    private final JwtUtils jwtUtils;

    public AuthInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是方法级别的请求，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireAuth requireAuth = getRequireAuth(handlerMethod);

        // 如果不需要认证，直接放行
        if (requireAuth == null) {
            return true;
        }

        // 获取token
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader == null || !authHeader.startsWith(tokenPrefix + " ")) {
            throw new BusinessException(401, "请先登录");
        }

        String token = authHeader.substring(tokenPrefix.length() + 1);

        try {
            // 解析token
            Claims claims = jwtUtils.parseToken(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            // 设置当前用户
            UserContext.setCurrentUser(new UserContext.CurrentUser(userId, username, role));

            // 检查角色权限
            String[] requiredRoles = requireAuth.value();
            if (requiredRoles.length > 0 && !Arrays.asList(requiredRoles).contains(role)) {
                throw new BusinessException(403, "权限不足");
            }

            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("认证失败: {}", e.getMessage());
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除用户上下文
        UserContext.clear();
    }

    /**
     * 获取RequireAuth注解
     */
    private RequireAuth getRequireAuth(HandlerMethod handlerMethod) {
        // 先从方法上获取
        RequireAuth methodAnnotation = handlerMethod.getMethodAnnotation(RequireAuth.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        // 再从类上获取
        return handlerMethod.getBeanType().getAnnotation(RequireAuth.class);
    }
}
