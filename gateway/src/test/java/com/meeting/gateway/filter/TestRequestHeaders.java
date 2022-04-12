package com.meeting.gateway.filter;

import com.meeting.common.entity.ResponseData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRequestHeaders {

    private final String url = "http://127.0.0.1:8000/testAuthority";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "eyJhbGciOiJIUzUxMiJ9.eyJjcmVhdGVkIjoxNjQ5NzQ1MjM4MjM2LCJyb2xlcyI6W3siaWQiOjEsImF1dGhvcml0eSI6InVzZXIifV0sImlkIjoxLCJleHAiOjE2NTAzNTAwMzgsImVtYWlsIjoiMjAxOTAwMzAxMDQxQG1haWwuc2R1LmVkdS5jbiIsInVzZXJuYW1lIjoidXNlciJ9.yj4xmzxMg0LcQ-mfa1WghmcABn1QFsjERauT6ITxsBIgsn3rqKpi7tjhh4Y1dtW89sI3nb_kuoX7EyQv6xs01Q");
        headers.add("user-agent", "PostmanRuntime/7.29.0");
        headers.add("accept", "*/*");
        headers.add("postman-token", "afc7bfe3-2a75-4836-916c-6b466413d2ba");
        headers.add("host", "127.0.0.1:8080");
        headers.add("accept-encoding", "gzip, deflate, br");
        headers.add("connection", "keep-alive");
        HttpEntity<String> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<ResponseData> exchange = restTemplate.exchange(url, HttpMethod.GET, httpEntity, ResponseData.class);
        ResponseData response = exchange.getBody();
        System.out.println(response.getCode());
        System.out.println(response.getMessage());
    }

}
