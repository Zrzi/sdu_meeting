package com.meeting.gateway.entity;

import java.util.Arrays;

public class Service {

    private String serviceName;
    private boolean authenticate;
    private boolean persistent;
    private String path;
    private String[] uri;
    private final AtomicCounter counter = new AtomicCounter();

    public Service() {}

    public Service(String serviceName, boolean authenticate, boolean persistent, String path, String[] uri) {
        this.serviceName = serviceName;
        this.authenticate = authenticate;
        this.persistent = persistent;
        this.path = path;
        this.uri = uri;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getUri() {
        return uri;
    }

    public void setUri(String[] uri) {
        this.uri = uri;
    }

    public AtomicCounter getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", authenticate=" + authenticate +
                ", path='" + path + '\'' +
                ", uri=" + Arrays.toString(uri) +
                '}';
    }

}
