package com.meeting.common.util;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Component
public class Md5Util {

    public String encrypt(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verify(String password, String passwordFromDatabase) {
        return passwordFromDatabase.equals(encrypt(password));
    }

}
