package com.meeting.controller;

import com.meeting.counter.ServiceIdCounter;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.Service;
import com.meeting.record.Consumers;
import com.meeting.record.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@CrossOrigin(origins = {"*"})
public class RegistryController {

    @Autowired
    private ServiceIdCounter counter;

    @Autowired
    private Providers providers;

    @Autowired
    private Consumers consumers;

    @ResponseBody
    @PostMapping(value = "/registerProvider", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData registerProvider(HttpServletRequest request,
                                         @RequestBody HashMap<String, Object> map) {
        if (!map.containsKey("name") || !map.containsKey("port")) {
            return new ResponseData(400, "参数错误");
        }
        try {
            String serviceName = (String) map.get("name");
            int port = (Integer) map.get("port");
            ResponseData responseData = null;
            Service service = new Service();
            Integer id = counter.getNext();
            service.setServiceId(id);
            service.setServiceName(serviceName);
            service.setIp(request.getRemoteAddr());
            service.setPort(port);
            if (providers.addRecord(service)) {
                responseData = new ResponseData(200, "ok");
                responseData.getData().put("id", id);
            } else {
                responseData = new ResponseData(400, "error");
            }
            return responseData;
        } catch (Exception exception) {
            return new ResponseData(400, "参数错误");
        }
    }

    @ResponseBody
    @PostMapping("/registerConsumer")
    public ResponseData registerConsumer(HttpServletRequest request,
                                         @RequestBody HashMap<String, Object> map) {
        if (!map.containsKey("name") || !map.containsKey("port")) {
            return new ResponseData(400, "参数错误");
        }
        try {
            String serviceName = (String) map.get("name");
            int port = (Integer) map.get("port");
            ResponseData responseData = null;
            Service service = new Service();
            Integer id = counter.getNext();
            service.setServiceId(id);
            service.setServiceName(serviceName);
            service.setIp(request.getRemoteAddr());
            service.setPort(port);
            if (consumers.addRecord(service)) {
                responseData = new ResponseData(200, "ok");
                responseData.getData().put("id", id);
            } else {
                responseData = new ResponseData(400, "error");
            }
            return responseData;
        } catch (Exception exception) {
            return new ResponseData(400, "参数错误");
        }
    }

}
