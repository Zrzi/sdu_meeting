package com.meeting.chatroom.entity;

import java.util.HashMap;
import java.util.Map;

public class ResponseData {

    public final static ResponseData ID_NOT_FOUND = new ResponseData(false, "MISSING_MESSAGE_ID");

    public final static ResponseData MESSAGE_TOO_LONG = new ResponseData(false, "MESSAGE_TOO_LONG");

    public final static ResponseData USER_ID_NOT_FOUND = new ResponseData(false, "MISSING_USER_ID");

    public final static ResponseData MESSAGE_NOT_EXIST = new ResponseData(false, "MESSAGE_NOT_EXIST");

    public final static ResponseData HAVE_ALREADY_REQUESTED = new ResponseData(true, "HAVE_ALREADY_REQUESTED");

    public final static ResponseData ILLEGAL_MESSAGE_FORMAT = new ResponseData(false, "ILLEGAL_MESSAGE_FORMAT");

    public final static ResponseData TYPE_NOT_ALLOWED = new ResponseData(false, "TYPE_NOT_ALLOWED");

    public final static ResponseData IS_ALREADY_FRIEND = new ResponseData(true, "IS_ALREADY_FRIEND");

    public final static ResponseData SERVER_PROBLEM = new ResponseData(false, "SERVER_PROBLEM");

    public final static ResponseData UNAUTHORIZED = new ResponseData(false, "UNAUTHORIZED");

    public final static ResponseData BAD_REQUEST = new ResponseData(false, "BAD_REQUEST");

    public final static ResponseData HEARTBEAT = new ResponseData(true, "HEARTBEAT");

    private boolean success;
    private String type;
    private final Map<String, Object> data = new HashMap<>();

    public ResponseData() {}

    public ResponseData(boolean success, String type) {
        this.success = success;
        this.type = type;
    }

    public static ResponseData ok(String message) {
        return new ResponseData(true, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
                "success=" + success +
                ", type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

}