package com.meeting.login_and_register.config;

import com.meeting.common.util.DigitUtil;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.common.util.Md5Util;
import com.meeting.common.util.UUIDUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilConfig {

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public Md5Util md5Util() {
        return new Md5Util();
    }

    @Bean
    public UUIDUtil uuidUtil() {
        return new UUIDUtil();
    }

    @Bean
    public DigitUtil digitUtil() {
        return new DigitUtil();
    }

}
