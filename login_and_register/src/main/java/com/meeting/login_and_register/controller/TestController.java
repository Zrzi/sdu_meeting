package com.meeting.login_and_register.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = {"*"})
public class TestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @ResponseBody
    @GetMapping("/testAuthority")
    public ResponseData testAuthority(@RequestHeader(value = "Authorization", required = false) String authorization,
                                      @RequestParam("value1") Integer value1,
                                      @RequestParam("value2") Integer value2) {
        System.out.println(value1);
        System.out.println(value2);
        if (authorization == null || !jwtTokenUtil.validateToken(authorization)) {
            return new ResponseData(403, "未登录");
        }
        System.out.println(authorization);
        return new ResponseData(200, "ok");
    }

}
