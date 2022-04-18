package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 设置响应
 */
@Order(1)
@Component
@WebFilter(filterName = "ResponseFilter", urlPatterns = {"/*"})
public class ResponseFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS.name())) {
            response.setStatus(200);
        } else {
            // 数据量大于8MB
            if (request.getContentLengthLong() > 8 * 1024 * 1024) {
                ResponseData responseData = new ResponseData(400, "数据过大");
                byte[] bytes = JSON.toJSONBytes(responseData);
                request.setAttribute("bytes", bytes);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            } else {

                filterChain.doFilter(request, response);
            }

            byte[] bytes = (byte[]) request.getAttribute("bytes");
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            out.close();

        }

    }
}
