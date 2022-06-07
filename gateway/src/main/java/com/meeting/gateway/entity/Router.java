package com.meeting.gateway.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Configuration
@PropertySource(value = {"classpath:/application.yml"}, encoding = "utf-8")
@ConfigurationProperties(prefix="router")
public class Router {

    private List<Service> services;

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    @PostConstruct
    public void postConstruct() {
        for (Service service : services) {
            service.init();
        }
    }

}
