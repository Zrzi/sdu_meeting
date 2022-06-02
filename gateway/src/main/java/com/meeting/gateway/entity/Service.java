package com.meeting.gateway.entity;

import com.meeting.gateway.balance.LoadBalancer;
import com.meeting.gateway.balance.impl.DefaultLoadBalancer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Service {

    private String serviceName;
    private String path;
    private String[] ip;
    private final AtomicCounter counter = new AtomicCounter();
    private final LoadBalancer balancer = new DefaultLoadBalancer();
    /**
     * 流量控制
     */
    private Integer controller = 10000;
    private final ReentrantLock lock = new ReentrantLock(false);

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

    /**
     * 判断是否还有容量
     * 如果controller == 0，返回false
     * 否则，controller自建，返回true
     * @return 是否需要降级
     */
    public boolean degraded() {
        lock.lock();
        try {
            if (controller == 0) {
                return false;
            }
            --controller;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 访问服务后，将controller自增
     */
    public void upgrade() {
        lock.lock();
        try {
            ++controller;
        } finally {
            lock.unlock();
        }
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
