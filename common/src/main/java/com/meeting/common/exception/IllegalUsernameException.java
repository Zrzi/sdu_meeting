package com.meeting.common.exception;

public class IllegalUsernameException extends RuntimeException {

    private String msg;

    public IllegalUsernameException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
