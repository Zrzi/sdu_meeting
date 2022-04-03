package com.meeting.log.controller;

import com.meeting.common.entity.ResponseData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LogController {

    private final Logger logger = LogManager.getLogger(LogController.class);

    @ResponseBody
    @PostMapping("/log")
    public ResponseData log(@RequestParam("log") String log) {
        logger.info(log);
        return new ResponseData(200, "ok");
    }

}
