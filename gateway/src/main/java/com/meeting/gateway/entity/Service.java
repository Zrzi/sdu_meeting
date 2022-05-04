package com.meeting.gateway.entity;

import com.meeting.gateway.balance.LoadBalancer;
import com.meeting.gateway.balance.impl.DefaultLoadBalancer;

import java.util.Arrays;

public class Service {

    private String serviceName;
    private String path;
    private String[] ip;
    private final AtomicCounter counter = new AtomicCounter();
    private final LoadBalancer balancer = new DefaultLoadBalancer();

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

    public boolean support(String uri) {
        return uri.startsWith(this.path);
    }

    public String getNextIp() {
        int index = balancer.getNextService(this.ip.length);
        return this.ip[index];
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
