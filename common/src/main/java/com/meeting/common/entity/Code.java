package com.meeting.common.entity;

public class Code {

    /**
     * 用户id
     */
    private Long id;

    /**
     * code类型
     * type == 1, 修改密码的code
     */
    private int type;

    /**
     * code字段
     */
    private String code;

    /**
     * 过期时间
     */
    private String date;

    /**
     * code状态
     * status == 0，新增的code，未被使用
     * status == 1，code已经被使用
     */
    private int status;

    public Code() {}

    public Code(Long id, int type, String code, int status) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Code{" +
                "id=" + id +
                ", type=" + type +
                ", code='" + code + '\'' +
                ", date='" + date + '\'' +
                ", status=" + status +
                '}';
    }

}
