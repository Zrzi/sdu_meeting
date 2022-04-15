package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.gateway.entity.Router;
import com.meeting.gateway.entity.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

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
        if (service == null) {
            responseData = new ResponseData(404, "不存在的服务");
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else {
            String url = buildUrl(request, getIp(service), service.getPath());
            try {
                if (Objects.equals(service.getServiceName(), "file") && Objects.equals(request.getMethod(), "GET")) {
                    RequestEntity<byte[]> requestEntity = buildRequestEntity(request, url);
                    byte[] bytes = restTemplate.exchange(requestEntity, byte[].class).getBody();
                    if (bytes != null) {
                        response.setContentType("image/" + url.substring(url.lastIndexOf('.') + 1));
                        OutputStream out = response.getOutputStream();
                        out.write(bytes);
                        out.flush();
                        out.close();
                        return;
                    } else {
                        responseData = new ResponseData(404, "文件不存在");
                    }
                } else {
                    RequestEntity<byte[]> requestEntity = buildRequestEntity(request, url);
                    responseData = restTemplate.exchange(requestEntity, ResponseData.class).getBody();
                }
            } catch (URISyntaxException exception) {
                responseData = new ResponseData(500, "服务器故障");
            }
        }
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(responseData));
        writer.flush();
        writer.close();
    }

    /**
     * 根据请求中的部分参数获取对应的Service对象
     * @param services 服务列表
     * @param uri 原始uri
     * @return Service对象
     */
    private Service getService(List<Service> services, String uri) {
        Service service = null;

        for (Service temp : services) {
            // todo 用正则表达式代替
            if (uri.startsWith(temp.getPath())) {
                service = temp;
                break;
            }
        }

        return service;
    }

    /**
     * 轮询ip地址
     * @param service 非空的Service对象
     * @return ip地址
     */
    private String getIp(Service service) {
        int index = service.getCounter().getAndIncrement();
        return service.getIp()[index % service.size()];
    }

    /**
     * 构造重定向的url
     * @param request 原始请求
     * @param ip 服务器ip
     * @param path 原始请求中的部分路径
     * @return 重定向的url
     */
    private String buildUrl(HttpServletRequest request, String ip, String path) {
        String query = request.getQueryString();
        return ip + request.getRequestURI().replace(path, "") +
                (query != null ? "?" + query : "");
    }

    /**
     * 创建RequestEntity<byte[]>
     * @param request 原始请求
     * @param url 重定向的url
     * @return RequestEntity\<byte[]\>对象
     * @throws IOException IO异常
     * @throws URISyntaxException 创建URI时抛出的异常
     */
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