package com.meeting.chatroom.entity;

import java.util.HashMap;
import java.util.Map;

public class ResponseData {

    public final static ResponseData ID_NOT_FOUND = new ResponseData(false, "id缺失");

    public final static ResponseData MESSAGE_TOO_LONG = new ResponseData(false, "消息过长");

    public final static ResponseData USER_ID_NOT_FOUND = new ResponseData(false, "用户id不存在");

    public final static ResponseData MESSAGE_NOT_EXIST = new ResponseData(false, "消息不存在");

    public final static ResponseData HAVE_ALREADY_REQUESTED = new ResponseData(true, "已经发送过");

    public final static ResponseData ILLEGAL_MESSAGE_FORMAT = new ResponseData(false, "格式错误");

    public final static ResponseData TYPE_NOT_ALLOWED = new ResponseData(false, "不支持的消息类型");

    private boolean success;
    private String message;
    private final Map<String, Object> data = new HashMap<>();

    public ResponseData() {}

    public ResponseData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ResponseData ok() {
        return new ResponseData(true, "ok");
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    @Override
    public String toString() {
        return "ResponseData{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}