package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于验证权限
 */
@Order(2)
@Component
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/*"})
public class AuthorizationFilter implements Filter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (!uri.contains("/login_and_register/login")
                && !uri.contains("/login_and_register/register")
                && !uri.contains("/login_and_register/code")
                && !uri.contains("/file/pic")) {
            // 检验token
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !jwtTokenUtil.validateToken(authorization)) {
                // 消息
                ResponseData responseData = new ResponseData(401, "未登录");
                byte[] bytes = JSON.toJSONBytes(responseData);
                request.setAttribute("bytes", bytes);
                response.setContentType("application/json");
                response.setStatus(401);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
