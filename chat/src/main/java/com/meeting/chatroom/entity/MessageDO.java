package com.meeting.chatroom.entity;

public class MessageDO {

    /**
     * 消息id
     */
    private long id;

    /**
     * 发送方id
     */
    private long fromId;

    /**
     * 接收方Id
     */
    private long toId;

    /**
     * 消息
     */
    private String message;

    /**
     * 时间
     */
    private long date;

    /**
     * status=0，消息未签收
     * status=1，消息已签收
     * status=2，好友请求，且没有被回复
     * status=3，好友请求，已经被回复
     */
    private int status;

    public MessageDO() {}

    public MessageDO(long id, long fromId, long toId, String message, long date, int status) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.message = message;
        this.date = date;
        this.status = status;
    }

    /**
     * 根据MessageVo对象构造MessageDo
     */
    public MessageDO(MessageVO messageVO, long fromId) {
        this.id = messageVO.getId() == null ? 0 : messageVO.getId();
        this.fromId = fromId;
        this.toId = messageVO.getToId();
        this.message = messageVO.getMessage();
        // 当前时间
        this.date = System.currentTimeMillis();
        // 未签收的消息
        this.status = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MessageDO{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", status=" + status +
                '}';
    }

}
