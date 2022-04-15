package com.meeting.common.exception;

public class UnAuthorizedException extends RuntimeException {

    private String msg;

    public UnAuthorizedException() {
        this.msg = "未登录";
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
