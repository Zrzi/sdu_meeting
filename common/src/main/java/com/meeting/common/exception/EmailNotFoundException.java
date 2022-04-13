package com.meeting.common.exception;

public class EmailNotFoundException extends RuntimeException {

    private String msg;

    public EmailNotFoundException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
