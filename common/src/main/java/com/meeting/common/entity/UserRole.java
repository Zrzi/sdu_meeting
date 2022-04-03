package com.meeting.common.entity;

public class UserRole {

    private Long uid;
    private Long rid;

    public UserRole() {}

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "uid=" + uid +
                ", rid=" + rid +
                '}';
    }

}
