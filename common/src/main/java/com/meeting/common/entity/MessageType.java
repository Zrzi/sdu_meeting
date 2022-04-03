package com.meeting.common.entity;

public enum MessageType {

    /**
     * 微服务向注册中心发送注册消息
     */
    REGISTRY,

    /**
     * 微服务向注册中心发送下线消息
     */
    CANCELLATION,

    /**
     * 注册中心向微服务发送的心跳检测消息
     */
    HEARTBEAT,

    /**
     * 注册中心向服务发送的信息消息
     */
    INFO,

}
