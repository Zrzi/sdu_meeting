package com.meeting.login_and_register.config;

import com.meeting.common.entity.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class Config {

    @Value("${server.port}")
    private int port;

    @Bean
    public ReadWriteLock serviceLock() {
        return new ReentrantReadWriteLock();
    }

    @Bean
    public Map<Integer, Service> services() {
        return new HashMap<Integer, Service>();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        System.out.println();
        return new TomcatServletWebServerFactory(port);
    }

}
