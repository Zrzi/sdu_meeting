package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.gateway.entity.Router;
import com.meeting.gateway.entity.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;

/**
 * 路由转发
 */
@Order(3)
@Component
@WebFilter(filterName = "RoutingFilter", urlPatterns = {"/*"})
public class RoutingFilter implements Filter {

    @Autowired
    private Router router;

    @Autowired
    private RestTemplate restTemplate;

    private final static Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ResponseData responseData = null;

        String uri = request.getRequestURI();
        Service service = getService(router.getServices(), uri);
        if (service == null) {
            logger.error("访问{}，服务不存在", uri);
            responseData = new ResponseData(404, "不存在的服务");
            response.setStatus(404);
        } else {
            if (service.degrade() <= 0) {
                logger.info("访问{}，服务降级", uri);
                responseData = new ResponseData(400, "服务繁忙，稍后再试");
                response.setStatus(400);
            } else {
                String url = buildUrl(request, service.getNextIp(), service.getPath());
                try {
                    RequestEntity<byte[]> requestEntity = buildRequestEntity(request, url);
                    ResponseEntity<byte[]> exchange = restTemplate.exchange(requestEntity, byte[].class);
                    byte[] bytes = exchange.getBody();
                    if (bytes != null) {
                        request.setAttribute("bytes", bytes);
                        response.setContentType(exchange.getHeaders().getContentType() != null
                                ? exchange.getHeaders().getContentType().toString()
                                : MediaType.APPLICATION_JSON_VALUE);
                        return;
                    } else {
                        logger.error("访问{}，资源不存在", url);
                        responseData = new ResponseData(404, "资源不存在");
                    }
                } catch (URISyntaxException exception) {
                    logger.error("访问{}，服务器故障，uri创建失败", url);
                    responseData = new ResponseData(500, "服务器故障，uri创建失败");
                    response.setStatus(500);
                } catch (ResourceAccessException exception) {
                    logger.error("访问{}，响应超时", url);
                    responseData = new ResponseData(500, "响应超时");
                    response.setStatus(500);
                } catch (HttpClientErrorException exception) {
                    logger.error("访问{}，出现异常{}", url, exception.getMessage());
                    responseData = new ResponseData(exception.getRawStatusCode(), exception.getMessage());
                    response.setStatus(exception.getRawStatusCode());
                }
            }
            service.upgrade();
        }
        byte[] bytes = JSON.toJSONBytes(responseData);
        request.setAttribute("bytes", bytes);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

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
            if (temp.support(uri)) {
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

    private byte[] read(InputStream input) {
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = -1;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

}