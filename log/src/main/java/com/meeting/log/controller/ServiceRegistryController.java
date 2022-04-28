package com.meeting.log.controller;

import com.meeting.common.entity.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin(origins = {"*"})
public class ServiceRegistryController {

    @ResponseBody
    @GetMapping("/heartbeat")
    public ResponseData heartbeat() {
        return new ResponseData(200, "ok");
    }

}
