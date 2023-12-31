package com.meeting.common.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String code;
    private List<Role> authorities;
    /**
     * status=0，表示用户已经注册，但未验证
     * status=1，表示用户已经注册，并且已经验证了
     */
    private int status;
    /**
     * profile为空字符串，表示用户使用默认头像
     * profile表示文件格式，支持png、jpg
     */
    private String profile;

    public User() {}

    public User(Long id, String username, String email, String password, String code, List<Role> authorities, int status, String profile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.code = code;
        this.authorities = authorities;
        this.status = status;
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Role> authorities) {
        this.authorities = authorities;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", id);
        map.put("username", username);
        map.put("email", email);
        map.put("profile", profile);
        return map;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", code='" + code + '\'' +
                ", authorities=" + authorities +
                ", status=" + status +
                ", profile='" + profile + '\'' +
                '}';
    }
}
