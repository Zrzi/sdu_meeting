package com.meeting.scheduler;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Consumers;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

@Component
public class Heartbeat {

    @Autowired
    private volatile Providers providers;

    @Autowired
    private volatile Consumers consumers;

    @Autowired
    private RestTemplate restTemplate;

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    private final ExecutorService pool = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @PostConstruct
    public void heartbeat() {
        service.scheduleWithFixedDelay(() -> {
            try {
                // 拷贝serviceMap中的keys
                int[] providerKeys = providers.keys();

                for (int key : providerKeys) {
                    Service service = providers.get(key);
                    if (service != null) {
                        pool.submit(
                                new ProviderTask(service.getServiceId(),
                                        service.getIp(), service.getPort())
                        );
                    }
                }

                // 拷贝serviceMap中的keys
                int[] consumerKeys = consumers.keys();

                for (int key : consumerKeys) {
                    Service service = consumers.get(key);
                    if (service != null) {
                        pool.submit(
                                new ConsumerTask(service.getServiceId(),
                                        service.getIp(), service.getPort())
                        );
                    }
                }
            } catch (URISyntaxException exception) {
                System.out.println(exception.getMessage());
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    class ProviderTask implements Runnable {

        private final int id;
        private final URI uri;

        public ProviderTask(int id, String ip, int port) throws URISyntaxException {
            this.id = id;
            // 本地测试使用
            // this.uri = new URI("http://" + "127.0.0.1" + ":" + port + "/heartbeat");
            this.uri = new URI("http://" + ip + ":" + port + "/heartbeat");
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

    class ConsumerTask implements Runnable {

        private final int id;
        private final URI uri;

        public ConsumerTask(int id, String ip, int port) throws URISyntaxException {
            this.id = id;
            // 本地测试使用
            // this.uri = new URI("http://" + "127.0.0.1" + ":" + port + "/heartbeat");
            this.uri = new URI("http://" + ip + ":" + port + "/heartbeat");
        }

        @Override
        public void run() {
            RequestEntity<String> requestEntity
                    = new RequestEntity<>(null, null, HttpMethod.GET, this.uri);
            try {
                ResponseEntity<ResponseData> exchange
                        = restTemplate.exchange(requestEntity, ResponseData.class);
                if (exchange.getStatusCode() != HttpStatus.OK) {
                    consumers.removeService(id);
                }
            } catch (ResourceAccessException | HttpClientErrorException exception) {
                consumers.removeService(id);
            }
        }

    }

}
