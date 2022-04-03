package com.meeting.common.exception;

public class UserExistException extends RuntimeException{

    private String msg;

    public UserExistException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
