package com.meeting.log.startup;

import com.meeting.common.entity.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Component
public class StartUpRunner implements CommandLineRunner {

    @Value("${server.port}")
    private int localPort;

    @Value("${registry.url}")
    private String url;

    @Value("${registry.log}")
    private String serviceName;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        String hostIp = getHostIp();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("service_name", serviceName);
        params.add("ip", hostIp);
        params.add("port", localPort);
        HttpEntity<MultiValueMap<String, Object>> httpEntity
                = new HttpEntity<>(params);
        ResponseEntity<ResponseData> responseEntity;
        ResponseData responseData;
        do {
            responseEntity
                    = restTemplate.postForEntity(url, httpEntity, ResponseData.class);
        } while (responseEntity.getStatusCode() != HttpStatus.OK
                || (responseData = responseEntity.getBody()) == null
                || responseData.getCode() != 200);
    }

    private String getHostIp() {
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address
                            // loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
