package com.meeting.chatroom.entity;

public class Friend {

    private Long uid;
    private Long friendId;

    public Friend() {}

    public Friend(Long uid, Long friendId) {
        this.uid = uid;
        this.friendId = friendId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "uid=" + uid +
                ", friendId=" + friendId +
                '}';
    }

}
