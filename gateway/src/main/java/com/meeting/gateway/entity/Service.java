package com.meeting.gateway.entity;

import java.util.Arrays;

public class Service {

    private String serviceName;
    private String path;
    private String[] uri;
    private Api[] apis;
    private final AtomicCounter counter = new AtomicCounter();

    public Service() {}

    public Service(String serviceName, String path, String[] uri, Api[] apis) {
        this.serviceName = serviceName;
        this.path = path;
        this.uri = uri;
        this.apis = apis;
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

    public String[] getUri() {
        return uri;
    }

    public void setUri(String[] uri) {
        this.uri = uri;
    }

    public Api[] getApis() {
        return apis;
    }

    public void setApis(Api[] apis) {
        this.apis = apis;
    }

    public AtomicCounter getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", path='" + path + '\'' +
                ", uri=" + Arrays.toString(uri) +
                ", apis=" + Arrays.toString(apis) +
                ", counter=" + counter +
                '}';
    }

}
