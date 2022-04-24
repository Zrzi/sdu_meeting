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
     * 获取历史记录，与num一起使用，表示从倒数第start条开始，一直读nun条
     */
    private Integer start;

    /**
     * 获取历史记录，与start一起使用，表示从倒数第start条开始，一直读nun条
     */
    private Integer num;

    /**
     * 消息类型
     * 从客户端发来的消息类型，参照MessageType
     * 返回给客户端的消息类型
     * type = 1，聊天消息
     * type = 2，好友请求
     * type = 3，回复请求
     */
    private Integer type;

    /**
     * 日期
     */
    private long date;

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

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
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

    @Override
    public String toString() {
        return "MessageVO{" +
                "id=" + id +
                ", toId=" + toId +
                ", message='" + message + '\'' +
                ", agree=" + agree +
                ", start=" + start +
                ", num=" + num +
                ", type=" + type +
                ", date=" + date +
                '}';
    }

}
