package com.meeting.test8900.controller;

import com.meeting.common.entity.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @ResponseBody
    @GetMapping("/loadbalancer")
    public ResponseData test() {
        return new ResponseData(200, "test 8900");
    }

}
