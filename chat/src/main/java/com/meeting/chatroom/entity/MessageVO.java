package com.meeting.chatroom.entity;

public class MessageVO {

    /**
     * message的id
     */
    private Long id;

    /**
     * 接收方的id
     */
    private Long toId;

    /**
     * 消息信息
     */
    private String message;

    /**
     * 回复好友请求，布尔
     */
    private boolean agree;

    /**
     * 日期
     */
    private long date;

    private String senderName;

    private Long sender;

    private Long receiver;

    private String sdp;

    private Integer accept;

    private String candidate;

    private String sdpMid;

    private Long sdpMLineIndex;

    private Long target;

    private String security;

    /**
     * 消息类型
     * 从客户端发来的消息类型，参照MessageType
     * 返回给客户端的消息类型
     * type = 1，聊天消息
     * type = 2，好友请求
     * type = 3，回复请求
     */
    private Integer type;

    public MessageVO() {}

    /**
     *  todo type应该可以不要了
     */
    public MessageVO(MessageDO message, int type) {
        this.id = message.getId();
        this.toId = message.getToId();
        this.message = message.getMessage();
        this.type = type;
        this.date = message.getDate();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAgree() {
        return agree;
    }

    public void setAgree(boolean agree) {
        this.agree = agree;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public Integer getAccept() {
        return accept;
    }

    public void setAccept(Integer accept) {
        this.accept = accept;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public Long getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(Long sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    @Override
    public String toString() {
        return "MessageVO{" +
                "id=" + id +
                ", toId=" + toId +
                ", message='" + message + '\'' +
                ", agree=" + agree +
                ", date=" + date +
                ", senderName='" + senderName + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", sdp='" + sdp + '\'' +
                ", accept=" + accept +
                ", candidate='" + candidate + '\'' +
                ", sdpMid='" + sdpMid + '\'' +
                ", sdpMLineIndex=" + sdpMLineIndex +
                ", target=" + target +
                ", security=" + security +
                ", type=" + type +
                '}';
    }

}
