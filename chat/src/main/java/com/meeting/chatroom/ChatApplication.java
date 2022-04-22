package com.meeting.chatroom;

import com.meeting.chatroom.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ChatApplication.class, args);
        SpringUtil.setApplicationContext(applicationContext);
    }

}
