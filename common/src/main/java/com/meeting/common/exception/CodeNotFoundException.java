package com.meeting.common.exception;

public class CodeNotFoundException extends RuntimeException{

    private String msg;

    public CodeNotFoundException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
