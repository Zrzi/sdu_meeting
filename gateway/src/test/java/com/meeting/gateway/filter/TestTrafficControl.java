package com.meeting.gateway.filter;

import com.meeting.common.entity.ResponseData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 对于testAuthority由于数据量较小，即使连续发送10000次请求，controller大约只会减小5个左右
 * 如果想要演示问题，需要将controller的初始值设置为2
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestTrafficControl {

    @Autowired
    private RestTemplate restTemplate;

    ExecutorService threadPool = Executors.newFixedThreadPool(100);

    @Test
    public void testTrafficControl() {
        String url = "http://127.0.0.1:8080/login_and_register/testAuthority?value1=111&value2=222";
        String authorization = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlt7ImlkIjoxLCJhdXRob3JpdHkiOiJ1c2VyIn1dLCJwcm9maWxlIjoicG5nIiwiaXNzIjoic2R1LW1lZXRpbmciLCJpZCI6MTUsImV4cCI6MTY1NTE2Mzc3NSwiaWF0IjoxNjU0NTU4OTc1LCJlbWFpbCI6IjIwMTkwMDMwMTA0MUBtYWlsLnNkdS5lZHUuY24iLCJ1c2VybmFtZSI6IumZiOe-pOefnCJ9.3UT9gJVe5FW3gas4ZMAKOodM9YVDshrV1YQaVgAcZnU";
        List<Future<ResponseData>> list = new ArrayList<Future<ResponseData>>();
        for (int i=0; i<100; ++i) {
            Future<ResponseData> result = threadPool.submit(new TaskCallable(url, authorization));
            list.add(result);
        }
        for (Future<ResponseData> result : list) {
            try {
                System.out.println(result.get().getMessage());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    class Task implements Runnable {

        private final String url;
        private final String authorization;

        public Task(String url, String authorization) {
            this.url = url;
            this.authorization = authorization;
        }

        @Override
        public void run() {
            try {
                HttpMethod method = HttpMethod.GET;
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", this.authorization);
                RequestEntity<byte[]> requestEntity = new RequestEntity<>(null, headers, method, new URI(url));
                ResponseEntity<ResponseData> response = restTemplate.exchange(requestEntity, ResponseData.class);
                System.out.println(response);
            } catch (URISyntaxException exception) {
                System.out.println(exception.getMessage());
            }
        }

    }

    class TaskCallable implements Callable<ResponseData> {

        private final String url;
        private final String authorization;

        public TaskCallable(String url, String authorization) {
            this.url = url;
            this.authorization = authorization;
        }

        @Override
        public ResponseData call() throws Exception {
            try {
                HttpMethod method = HttpMethod.GET;
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", this.authorization);
                RequestEntity<byte[]> requestEntity = new RequestEntity<>(null, headers, method, new URI(url));
                ResponseEntity<ResponseData> response = restTemplate.exchange(requestEntity, ResponseData.class);
                return response.getBody();
            } catch (URISyntaxException exception) {
                return new ResponseData(500, "uri语法出错");
            }
        }

    }


}
