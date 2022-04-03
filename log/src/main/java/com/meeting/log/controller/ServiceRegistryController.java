package com.meeting.log.controller;

import com.meeting.common.entity.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ServiceRegistryController {

    @ResponseBody
    @GetMapping("/heartBeat")
    public ResponseData heartBeat() {
        return new ResponseData(200, "ok");
    }

}
