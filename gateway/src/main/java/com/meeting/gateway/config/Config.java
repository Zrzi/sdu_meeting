package com.meeting.gateway.config;

import com.meeting.common.util.JwtTokenUtil;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class Config {

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(Duration.ofMillis(8 * 1000));
        builder.setReadTimeout(Duration.ofMillis(8 * 1000));
        return builder.build();
    }

}
