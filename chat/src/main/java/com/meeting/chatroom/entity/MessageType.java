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
    REPLY(4, "回复好友请求"),

    /**
     * 获取好友信息
     */
    PULL_FRIENDS(5, "获取好友信息"),

    /**
     * 获取好友请求
     */
    PULL_REQUESTS(6, "获取好友请求"),

    /**
     * 获取未签收消息
     */
    PULL_UNSIGNED_MESSAGE(7, "获取未签收消息"),

    /**
     * 获取历史消息记录
     */
    PULL_HISTORY_MESSAGE(8, " 获取历史消息记录");

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
