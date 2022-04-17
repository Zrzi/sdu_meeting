package com.meeting.controller;

import com.meeting.counter.ServiceIdCounter;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Consumers;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RegistryController {

    @Autowired
    private ServiceIdCounter counter;

    @Autowired
    private Providers providers;

    @Autowired
    private Consumers consumers;

    @ResponseBody
    @PostMapping("/registerProvider")
    public ResponseData registerProvider(@RequestParam("serviceName") String serviceName,
                                         @RequestParam("ip") String ip,
                                         @RequestParam("port") Integer port) {
        ResponseData responseData;
        Service service = new Service();
        Integer id = counter.getNext();
        service.setServiceId(id);
        service.setServiceName(serviceName);
        service.setIp(ip);
        service.setPort(port);
        if (providers.addRecord(service)) {
            responseData = new ResponseData(200, "ok");
            responseData.getData().put("id", id);
        } else {
            responseData = new ResponseData(400, "error");
        }
        return responseData;
    }

    @ResponseBody
    @PostMapping("/registerConsumer")
    public ResponseData registerConsumer(@RequestParam("service_name") String serviceName,
                                         @RequestParam("ip") String ip,
                                         @RequestParam("port") Integer port) {
        ResponseData responseData;
        Service service = new Service();
        Integer id = counter.getNext();
        service.setServiceId(id);
        service.setServiceName(serviceName);
        service.setIp(ip);
        service.setPort(port);
        if (consumers.addRecord(service)) {
            responseData = new ResponseData(200, "ok");
            responseData.getData().put("id", id);
        } else {
            responseData = new ResponseData(400, "error");
        }
        return responseData;
    }

}
