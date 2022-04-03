package com.meeting.scheduler;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

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
    private void heartBeat() {

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
        private final String url;

        public Task(int id, String ip, int port) {
            this.id = id;
            this.url = "http://" + ip + "/" + port + "/heartBeat";
        }

        @Override
        public void run() {
            ResponseEntity<ResponseData> exchange =
                    restTemplate.exchange(this.url, HttpMethod.GET, null, ResponseData.class);
            if (exchange.getStatusCode() != HttpStatus.OK) {
                providers.removeService(id);
            }
        }

    }

}
