package com.meeting.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.gateway.entity.Router;
import com.meeting.gateway.entity.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;

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
        ResponseData responseData = new ResponseData();

        // 挑选对应的service
        // todo 用正则表达式来替代
        List<Service> services = router.getServices();
        Service service = null;
        String path = null;
        for (Service temp : services) {
            String url = httpServletRequest.getRequestURI();
            if (url.contains(temp.getPath())) {
                path = url.split(temp.getPath())[1];
                service = temp;
                break;
            }
        }

        if (service == null) {
            responseData.setCode(403);
            responseData.setMessage("不存在的路径");
        } else {
            // 需要权限校验
            String token = null;
            if (service.isAuthenticate() &&
                    ((token = httpServletRequest.getHeader("token")) == null ||
                            jwtTokenUtil.validateToken(token))) {
                responseData.setCode(404);
                responseData.setMessage("没有权限");
            } else {
                // 获取下一个的uri
                int index = service.getCounter().autoIncrement();
                String uri = service.getUri()[index % service.getUri().length];
                if (service.isPersistent()) {
                    // 建立长连接，特别处理
                    // todo url待修正
                    responseData.setCode(200);
                    responseData.getData().put("ip", service.getUri()[index % service.getUri().length]);
                    responseData.setMessage("ok");
                } else {
                    String url = uri + path;
                    // 获取请求头、请求参数
                    HttpEntity<MultiValueMap<String, Object>> httpEntity =
                            buildHttpRequest(httpServletRequest);
                    responseData = handle(httpEntity, httpServletRequest.getMethod(), url);
                }
            }
        }
        PrintWriter printWriter = httpServletResponse.getWriter();
        printWriter.write(JSON.toJSONString(responseData));
        printWriter.flush();
        printWriter.close();
    }

    /**
     * 请求头、请求参数
     * @param request 原始请求
     * @return map
     */
    private HttpEntity<MultiValueMap<String, Object>> buildHttpRequest(HttpServletRequest request) {

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            headers.add(name, value);
        }

        // 请求参数
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String[] value = request.getParameterValues(name);
            if (value.length == 1) {
                parameters.add(name, value[0]);
            } else {
                parameters.add(name, value);
            }
        }

        return new HttpEntity<>(parameters, headers);
    }

    private ResponseData handle(HttpEntity<MultiValueMap<String, Object>> httpEntity, String method, String url) {
        ResponseData responseData = new ResponseData();
        switch (method) {
            case "GET":
                responseData =
                        restTemplate.exchange(url, HttpMethod.GET, httpEntity, ResponseData.class).getBody();
                break;
            case "POST":
                responseData =
                        restTemplate.exchange(url, HttpMethod.POST, httpEntity, ResponseData.class).getBody();
                break;
            case "PUT":
                // todo httpClient
                break;
            case "DELETE":
                // todo httpClient
                break;
            default:
                responseData.setCode(400);
                responseData.setMessage("不支持的方法");
                break;
        }
        return responseData;
    }

}
