package com.meeting.common.exception;

public class UsernameNotFoundException extends RuntimeException {

    private String msg;

    public UsernameNotFoundException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
