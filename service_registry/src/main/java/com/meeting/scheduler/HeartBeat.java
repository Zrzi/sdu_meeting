package com.meeting.scheduler;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

@Configuration
@EnableScheduling
public class HeartBeat {

    @Autowired
    private Providers providers;

    @Autowired
    private RestTemplate restTemplate;

    private ExecutorService pool = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @Scheduled(fixedDelay = 30000)
    private void heartBeat() throws URISyntaxException {

        // 拷贝serviceMap中的keys
        int[] keys = providers.keys();

        for (int key : keys) {
            Service service = providers.get(key);
            if (service != null) {
                pool.execute(
                        new Task(service.getServiceId(),
                                service.getIp(), service.getPort())
                );
            }
        }

    }

    class Task implements Runnable {

        private final int id;
        private final URI uri;

        public Task(int id, String ip, int port) throws URISyntaxException {
            this.id = id;
            // 本地测试使用
            this.uri = new URI("http://" + "127.0.0.1" + ":" + port + "/heartBeat");
            // this.uri = new URI("http://" + ip + "/" + port + "/heartBeat");
        }

        @Override
        public void run() {
            RequestEntity<String> requestEntity
                    = new RequestEntity<>(null, null, HttpMethod.GET, this.uri);
            try {
                ResponseEntity<ResponseData> exchange
                        = restTemplate.exchange(requestEntity, ResponseData.class);
                if (exchange.getStatusCode() != HttpStatus.OK) {
                    providers.removeService(id);
                }
            } catch (ResourceAccessException | HttpClientErrorException exception) {
                providers.removeService(id);
            }
        }

    }

}
