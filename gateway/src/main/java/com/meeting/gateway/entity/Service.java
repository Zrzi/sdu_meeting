package com.meeting.gateway.entity;

import com.meeting.gateway.balance.LoadBalancer;
import com.meeting.gateway.balance.impl.DefaultLoadBalancer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Service {

    private String serviceName;
    private String path;
    private String[] ip;
    private final AtomicCounter counter = new AtomicCounter();

    /**
     * LoadBalancer的类型
     */
    private String balancerClass;
    private LoadBalancer balancer;

    /**
     * 流量控制初始值
     */
    private int controllerInit;
    /**
     * 流量控制初始值
     */
    private AtomicInteger controller;

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

    public String getBalancerClass() {
        return balancerClass;
    }

    public void setBalancerClass(String balancerClass) {
        this.balancerClass = balancerClass;
    }

    public int getControllerInit() {
        return controllerInit;
    }

    public void setControllerInit(int controllerInit) {
        this.controllerInit = controllerInit;
    }

    public void init() {
        this.controller = new AtomicInteger(this.controllerInit);
        try {
            this.balancer = (LoadBalancer) Class.forName(this.balancerClass).newInstance();
        } catch (ClassNotFoundException  | InstantiationException | IllegalAccessException exception) {
            this.balancer = new DefaultLoadBalancer();
        }
    }

    /**
     * 返回容量
     * 如果controller <= 0，没有容量，服务降级
     * 否则，还有容量，继续执行
     * @return 剩余容量
     */
    public int degrade() {
        return controller.decrementAndGet();
    }

    /**
     * controller自增
     */
    public void upgrade() {
        controller.incrementAndGet();
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
