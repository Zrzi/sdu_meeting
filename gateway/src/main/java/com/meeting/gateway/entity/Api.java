package com.meeting.gateway.entity;

public class Api {

    private String path;
    private boolean authenticate;
    private boolean persistent;

    public Api() {}

    public Api(String path, boolean authenticate, boolean persistent) {
        this.path = path;
        this.authenticate = authenticate;
        this.persistent = persistent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public String toString() {
        return "Api{" +
                "path='" + path + '\'' +
                ", authenticate=" + authenticate +
                ", persistent=" + persistent +
                '}';
    }

}
