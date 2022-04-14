package com.meeting.gateway.entity;

import java.util.Arrays;

public class Service {

    private String serviceName;
    private String path;
    private String[] ip;
    private final AtomicCounter counter = new AtomicCounter();

    public Service() {}

    public Service(String serviceName, String path, String[] ip) {
        this.serviceName = serviceName;
        this.path = path;
        this.ip = ip;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getIp() {
        return ip;
    }

    public void setIp(String[] ip) {
        this.ip = ip;
    }

    public AtomicCounter getCounter() {
        return counter;
    }

    public int size() {
        return this.ip.length;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", path='" + path + '\'' +
                ", ip=" + Arrays.toString(ip) +
                ", counter=" + counter +
                '}';
    }

}
