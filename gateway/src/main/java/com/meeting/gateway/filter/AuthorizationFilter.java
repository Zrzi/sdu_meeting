package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用于验证权限
 */
@Order(0)
@Component
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = {"/*"})
public class AuthorizationFilter implements Filter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String uri = request.getRequestURI();
        if (!uri.contains("/login_and_register/login")
                && !uri.contains("/login_and_register/register")
                && !uri.contains("/login_and_register/code")
                && !uri.contains("/file/pic")) {
            // 检验token
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !jwtTokenUtil.validateToken(authorization)) {
                // 设置响应头
                response.setContentType("application/json");
                // 消息
                ResponseData responseData = new ResponseData(403, "未登录");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                PrintWriter printWriter = response.getWriter();
                printWriter.write(JSON.toJSONString(responseData));
                printWriter.flush();
                printWriter.close();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
