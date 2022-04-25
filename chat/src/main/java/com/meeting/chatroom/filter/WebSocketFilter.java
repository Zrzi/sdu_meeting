package com.meeting.chatroom.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@Component
@WebFilter(filterName = "WebSocketFilter", urlPatterns = {"/ws"})
public class WebSocketFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 设置Sec-WebSocket-Protocol
        String token = request.getHeader("Sec-WebSocket-Protocol");
        System.out.println(token);
        response.setHeader("Sec-WebSocket-Protocol", token);

        filterChain.doFilter(request, response);
    }

}
