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
     * 用户发起会话请求
     */
    PRIVATE_WEBRTC_OFFER(5, "private_webrtc_offer"),

    /**
     * 响应会话请求
     */
    PRIVATE_WEBRTC_ANSWER(6, "private_webrtc_answer"),

    /**
     * ICE候选者
     */
    PRIVATE_WEBRTC_CANDIDATE(7, "private_webrtc_candidate"),

    /**
     * 挂断电话
     */
    PRIVATE_WEBRTC_DISCONNECT(8, "private_webrtc_disconnect"),

    /**
     * 用户发起会话请求（sender）
     * 发送视频通话请求
     */
    REQUEST_WEBRTC(9, "REQUEST_WEBRTC"),

    /**
     * 用户发起绘画请求（receiver）
     * 应答视频通话请求
     */
    ANSWER_WEBRTC(10, "ANSWER_WEBRTC"),

    /**
     * 心跳
     */
    HEARTBEAT(11, "heartbeat"),

    /**
     * 删除好友
     */
    REMOVE_FRIEND(12, "remove_friend"),

    /**
     * 响应好友删除
     */
    REPLY_REMOVE_FRIEND(13, "reply_remove_friend");

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
