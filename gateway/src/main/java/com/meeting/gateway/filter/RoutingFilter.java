package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.exception.PathNotFoundException;
import com.meeting.gateway.entity.Router;
import com.meeting.gateway.entity.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;

/**
 * 路由转发
 */
@Order(1)
@Component
@WebFilter(filterName = "RoutingFilter", urlPatterns = {"/*"})
public class RoutingFilter implements Filter {

    @Autowired
    private Router router;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        ResponseData responseData;
        String uri = request.getRequestURI();
        Service service = getService(router.getServices(), uri);
        String url = buildUrl(request, getIp(service), service.getPath());
        try {
            RequestEntity<byte[]> requestEntity = buildRequestEntity(request, url);
            responseData = restTemplate.exchange(requestEntity, ResponseData.class).getBody();
        } catch (URISyntaxException e) {
            responseData = new ResponseData(500, "服务器故障");
        }
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(responseData));
        writer.flush();
        writer.close();
    }

    private Service getService(List<Service> services, String uri)
            throws PathNotFoundException {
        Service service = null;
        for (Service temp : services) {
            // todo 用正则表达式代替
            if (uri.startsWith(temp.getPath())) {
                service = temp;
                break;
            }
        }

        if (service == null) {
            throw new PathNotFoundException("不存在的服务");
        }

        return service;
    }

    private String getIp(Service service) {
        int index = service.getCounter().getAndIncrement();
        return service.getIp()[index % service.size()];
    }

    private String buildUrl(HttpServletRequest request, String ip, String path) {
        String query = request.getQueryString();
        return ip + request.getRequestURI().replace(path, "") +
                (query != null ? "?" + query : "");
    }

    private RequestEntity<byte[]> buildRequestEntity(HttpServletRequest request, String url) throws IOException, URISyntaxException {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        HttpHeaders header = parseRequestHeaders(request);
        byte[] body = parseRequestBody(request);
        return new RequestEntity<byte[]>(body, header, method, new URI(url));
    }

    /**
     * 构建请求头
     * @param request 原始请求
     * @return 请求头对象
     */
    private HttpHeaders parseRequestHeaders(HttpServletRequest request) {
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.add(name, value);
        }
        return headers;
    }

    /**
     * 构建请求体
     * @param request 原始请求
     * @return byte[]数组
     * @throws IOException IO异常
     */
    private byte[] parseRequestBody(HttpServletRequest request)
            throws IOException {
        InputStream input = request.getInputStream();
        return StreamUtils.copyToByteArray(input);
    }

}
