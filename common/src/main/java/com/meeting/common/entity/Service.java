package com.meeting.common.entity;

public class Service {

    /**
     * 微服务id
     */
    private Integer serviceId;

    /**
     * 微服务名称
     */
    private String serviceName;

    /**
     * 微服务ip地址
     */
    private String ip;

    /**
     * 微服务端口
     */
    private Integer port;

    public Service() {}

    public Service(Integer serviceId, String serviceName, String ip, Integer port) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

}
