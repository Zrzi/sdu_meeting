package com.meeting.file.config;

import com.meeting.common.util.JwtTokenUtil;
import com.meeting.common.util.UUIDUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfig {

    @Bean
    public UUIDUtil uuidUtil() {
        return new UUIDUtil();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

}
