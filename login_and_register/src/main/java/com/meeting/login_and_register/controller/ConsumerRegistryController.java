package com.meeting.login_and_register.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

@Controller
@CrossOrigin(origins = {"*"})
public class ConsumerRegistryController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Map<Integer, Service> services;

    @Autowired
    private ReadWriteLock lock;

    @ResponseBody
    @GetMapping("/heartbeat")
    public ResponseData heartbeat() {
        return new ResponseData(200, "ok");
    }

    @ResponseBody
    @PostMapping("/service/list")
    public ResponseData updateInfo(@RequestParam("serviceList") String message) {
        lock.writeLock().lock();
        try {
            services = JSON.parseObject(message, new TypeReference<Map<Integer, Service>>(){});
            System.out.println(services);
        } finally {
            lock.writeLock().unlock();
        }
        return new ResponseData(200, "ok");
    }

}
