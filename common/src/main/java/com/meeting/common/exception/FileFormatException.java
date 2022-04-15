package com.meeting.common.exception;

public class FileFormatException extends RuntimeException {

    private String msg;

    public FileFormatException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
