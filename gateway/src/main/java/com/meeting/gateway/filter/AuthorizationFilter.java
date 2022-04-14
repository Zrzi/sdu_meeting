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
        // 设置响应头
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");

        String uri = request.getRequestURI();
        if (!uri.contains("/login_and_register/login")
                && !uri.contains("/login_and_register/register")
                && !uri.contains("/login_and_register/code")) {
            // 检验token
            String authorization = request.getHeader("authorization");
            if (authorization == null || !jwtTokenUtil.validateToken(authorization)) {
                ResponseData responseData = new ResponseData(403, "未登录");
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
