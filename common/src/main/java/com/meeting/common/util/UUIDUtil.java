package com.meeting.common.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtil {

    public String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
