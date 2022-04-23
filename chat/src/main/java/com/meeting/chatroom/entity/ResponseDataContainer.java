package com.meeting.chatroom.entity;

/**
 * 容纳ResponseData的容器
 * toSender 回复给sender的响应
 * toReceiver 回复给receiver的响应
 */
public class ResponseDataContainer {

    /**
     * 回复给sender的响应
     */
    private ResponseData toSender;

    /**
     * 回复给receiver的响应
     */
    private ResponseData toReceiver;

    public ResponseDataContainer() {}

    public ResponseDataContainer(ResponseData toSender, ResponseData toReceiver) {
        this.toSender = toSender;
        this.toReceiver = toReceiver;
    }

    public ResponseData getToSender() {
        return toSender;
    }

    public void setToSender(ResponseData toSender) {
        this.toSender = toSender;
    }

    public ResponseData getToReceiver() {
        return toReceiver;
    }

    public void setToReceiver(ResponseData toReceiver) {
        this.toReceiver = toReceiver;
    }

    @Override
    public String toString() {
        return "ResponseDataContainer{" +
                "toSender=" + toSender +
                ", toReceiver=" + toReceiver +
                '}';
    }

}
