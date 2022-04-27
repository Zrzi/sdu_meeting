package com.meeting.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class Config {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // 设置最大等待时间
        builder.setConnectTimeout(Duration.ofMillis(500));
        builder.setReadTimeout(Duration.ofMillis(500));
        return builder.build();
    }

}
