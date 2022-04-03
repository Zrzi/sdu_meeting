package com.meeting.common.entity;

import java.util.HashMap;
import java.util.Map;

public class ResponseData {

    private int code;
    private String message;
    private final Map<String, Object> data = new HashMap<>();

    public ResponseData() {}

    public ResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

}
