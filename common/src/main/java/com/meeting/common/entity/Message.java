package com.meeting.common.entity;

import java.util.HashMap;
import java.util.Map;

public class Message {

    private Integer serviceId;
    private MessageType messageType;
    private final Map<String, Object> message = new HashMap<>();

    public Message() {}

    public Message(Integer serviceId, MessageType messageType) {
        this.serviceId = serviceId;
        this.messageType = messageType;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Map<String, Object> getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "serviceId=" + serviceId +
                ", messageType=" + messageType +
                ", message=" + message +
                '}';
    }

}
