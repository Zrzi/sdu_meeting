package com.meeting.scheduler;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Consumers;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class UpdateInfo {

    @Autowired
    private Consumers consumers;

    @Autowired
    private Providers providers;

    @Autowired
    private RestTemplate restTemplate;

    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    private final ExecutorService pool = new ThreadPoolExecutor(10, 200, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @PostConstruct
    public void updateInfo() {
        service.scheduleWithFixedDelay(() -> {
            try {
                boolean modified = providers.isModified();
                if (modified) {
                    Map<Integer, Service> copy = providers.getAllCopy();

                    // 拷贝serviceMap中的keys
                    int[] keys = consumers.keys();

                    for (int key : keys) {
                        Service service = consumers.get(key);
                        if (service != null) {
                            pool.execute(
                                    new Task(service.getServiceId(),
                                            service.getIp(), service.getPort(), copy)
                            );
                        }
                    }

                    providers.modify();

                }
            } catch (URISyntaxException exception) {
                System.out.println(exception.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    class Task implements Runnable {

        private final int id;
        private final URI uri;
        private final Map<Integer, Service> copy;

        public Task(int id, String ip, int port, Map<Integer, Service> copy) throws URISyntaxException {
            this.id = id;
            // 本地测试
            // this.uri = new URI("http://" + "127.0.0.1" + ":" + port + "/service/list");
            this.uri = new URI("http://" + ip + ":" + port + "/service/list");
            this.copy = copy;
        }

        @Override
        public void run() {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add("serviceList", copy);
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
