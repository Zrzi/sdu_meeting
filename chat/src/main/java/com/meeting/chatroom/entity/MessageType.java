package com.meeting.chatroom.entity;

public enum MessageType {

    /**
     * 聊天消息
     */
    CHAT(1, "聊天消息"),

    /**
     * 消息签收
     */
    SIGNED(2, "消息签收"),

    /**
     * 请求添加好友
     */
    REQUEST(3, "请求添加好友"),

    /**
     * 回复好友请求
     */
    REPLY(4, "回复好友请求");

    private final int type;
    private final String text;

    MessageType(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

}
