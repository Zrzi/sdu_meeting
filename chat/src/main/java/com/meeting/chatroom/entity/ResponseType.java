package com.meeting.chatroom.entity;

public enum ResponseType {

    /**
     * 给消息发送方的成功响应
     */
    MESSAGE_SENDER_OK("MESSAGE_SENDER_OK"),

    /**
     * 给消息接受方的成功响应
     */
    MESSAGE_RECEIVER_OK("MESSAGE_RECEIVER_OK"),

    /**
     * 消息签收成功的响应
     */
    SIGN_OK("SIGN_OK"),

    /**
     * 请求未签收消息的响应
     */
    UNSIGNED_MESSAGE("UNSIGNED_MESSAGE_OK"),

    /**
     * 请求历史消息的响应
     */
    HISTORY_MESSAGE("HISTORY_MESSAGE"),

    /**
     * 请求好友列表的响应
     */
    FRIEND("FRIEND"),

    /**
     * 发送好友请求成功的响应
     */
    REQUEST_SENDER_OK("REQUEST_SENDER_OK"),

    /**
     * 收到好友请求的响应
     */
    REQUEST_RECEIVER_OK("REQUEST_RECEIVER_OK"),

    /**
     * 收到好友回复的消息
     */
    REPLY_SENDER_OK("REPLY_SENDER_OK"),

    /**
     * 回复好友请求成功的消息
     */
    REPLY_RECEIVER_OK("REPLY_RECEIVER_OK"),

    /**
     * 请求未回复的好友请求
     */
    REQUESTS_TO_BE_REPLIED("REQUESTS_TO_BE_REPLIED");

    private String type;

    private ResponseType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
