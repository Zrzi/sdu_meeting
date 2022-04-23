package com.meeting.chatroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                // 允许请求头，*号表示任何请求头
                .allowedHeaders("*")
                // 允许任何方法（post、get等）
                .allowedMethods("*")
                // 允许跨域的域名，*表示允许任何域名使用
                .allowedOrigins("*")
                // 表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
                .maxAge(3600);
    }

}
