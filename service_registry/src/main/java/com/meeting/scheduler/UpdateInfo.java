package com.meeting.scheduler;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Consumers;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

@Configuration
@EnableScheduling
public class UpdateInfo {

    @Autowired
    private Consumers consumers;

    @Autowired
    private Providers providers;

    @Autowired
    private RestTemplate restTemplate;

    private final ExecutorService pool = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @Scheduled(fixedDelay = 45000)
    private void updateInfo() throws URISyntaxException {

        String message = providers.getAllCopy();

        // 拷贝serviceMap中的keys
        int[] keys = consumers.keys();

        for (int key : keys) {
            Service service = consumers.get(key);
            if (service != null) {
                pool.execute(
                        new Task(service.getServiceId(),
                                service.getIp(), service.getPort(), message)
                );
            }
        }

    }

    class Task implements Runnable {

        private final int id;
        private final URI uri;
        private final String message;

        public Task(int id, String ip, int port, String message) throws URISyntaxException {
            this.id = id;
            this.uri = new URI("http://" + ip + "/" + port + "/updateInfo");
            this.message = message;
        }

        @Override
        public void run() {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("message", message);
            HttpEntity<MultiValueMap<String, Object>> httpEntity
                    = new HttpEntity<>(params);
            ResponseEntity<ResponseData> response
                    = restTemplate.postForEntity(uri, httpEntity, ResponseData.class);
            try {
                if (response.getStatusCode() != HttpStatus.OK) {
                    consumers.removeService(id);
                }
            } catch (ResourceAccessException exception) {
                consumers.removeService(id);
            }
        }

    }

}
