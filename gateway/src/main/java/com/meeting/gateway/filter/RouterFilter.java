package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.exception.PathNotFoundException;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.gateway.entity.Api;
import com.meeting.gateway.entity.Router;
import com.meeting.gateway.entity.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebFilter(filterName = "RouterFilter", urlPatterns = {"/*"})
@Component
public class RouterFilter implements Filter {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Router router;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        // 跨域设置
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        ResponseData responseData;
        // uri格式 /login_and_register/login
        String uri = httpServletRequest.getRequestURI();

        // 挑选对应的service
        try {
            Service service = getService(router.getServices(), uri);
            Api api = getApi(service.getApis(), uri);

            // 需要权限校验
            String token = null;
            // 如果需要验证权限
            if (api.isAuthenticate() &&
                    ((token = httpServletRequest.getHeader("token")) == null ||
                            !jwtTokenUtil.validateToken(token))) {
                // token不存在或者token过期，无权限
                responseData = new ResponseData(403, "无权限");
                sendResponseData(httpServletResponse, responseData);
                return;
            }
            if (api.isPersistent()) {
                // todo 处理长连接
            } else {
                // 获取索引，轮询
                int index = service.getCounter().getAndIncrement();
                String url = service.getUri()[index % service.getUri().length] + api.getPath();
                // 获取请求头、请求参数
                HttpHeaders headers = buildHeaders(httpServletRequest);
                MultiValueMap<String, String> parameters = buildParameters(httpServletRequest);
                responseData = handle(headers, parameters, httpServletRequest.getMethod(), url);
                sendResponseData(httpServletResponse, responseData);
            }
        } catch (PathNotFoundException exception) {
            responseData = new ResponseData(404, exception.getMsg());
            sendResponseData(httpServletResponse, responseData);
        }
    }


    /**
     * 构建请求头
     * @param request 原始请求
     * @return 请求头对象
     */
    private HttpHeaders buildHeaders(HttpServletRequest request) {
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
     * 构建请求参数
     * @param request request 原始请求
     * @return 请求参数对象
     */
    private MultiValueMap<String, String> buildParameters(HttpServletRequest request) {
        // 请求参数
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            parameters.add(name, value);
        }
        return parameters;
    }

    private ResponseData handle(HttpHeaders headers, MultiValueMap<String, String> parameters, String method, String url) {
        ResponseData responseData = new ResponseData();
        HttpEntity<MultiValueMap<String, String>> httpEntity;
        switch (method) {
            case "GET":
                httpEntity = new HttpEntity<>(null, headers);
                UriComponentsBuilder builder =
                        UriComponentsBuilder
                                .fromUriString(url)
                                .queryParams(parameters);
                UriComponents uriComponents = builder.build().encode();
                responseData =
                        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, httpEntity, ResponseData.class).getBody();
                break;
            case "POST":
                 httpEntity
                        = new HttpEntity<>(parameters, headers);
                responseData =
                        restTemplate.exchange(url, HttpMethod.POST, httpEntity, ResponseData.class).getBody();
                break;
            case "PUT":
                // todo httpClient
                // 目前项目中没有PUT请求
                break;
            case "DELETE":
                // todo httpClient
                // 目前项目中没有DELETE请求
                break;
            default:
                // 不支持其它类型的请求
                responseData.setCode(400);
                responseData.setMessage("不支持的方法");
                break;
        }
        return responseData;
    }

    private void sendResponseData(HttpServletResponse response, ResponseData responseData)
            throws IOException {
        PrintWriter printWriter = response.getWriter();
        printWriter.write(JSON.toJSONString(responseData));
        printWriter.flush();
        printWriter.close();
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

    private Api getApi(Api[] apis, String uri) {
        Api api = null;
        for (Api temp : apis) {
            // todo 用正则表达式代替
            if (temp.getPath() != null && uri.endsWith(temp.getPath())) {
                api = temp;
                break;
            }
        }

        if (api == null) {
            throw new PathNotFoundException("不存在的路径");
        }

        return api;
    }

}
