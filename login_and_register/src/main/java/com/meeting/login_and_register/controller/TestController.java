package com.meeting.login_and_register.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin(origins = {"*"})
public class TestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @ResponseBody
    @GetMapping("/testAuthority")
    public ResponseData testAuthority(@RequestHeader(value = "token", required = false) String token) {
        if (token == null) {
            return new ResponseData(404, "无权限");
        }
        System.out.println(token);
        Boolean flag = jwtTokenUtil.validateToken(token);
        if (flag) {
            return new ResponseData(200, "ok");
        } else {
            return new ResponseData(404, "无权限");
        }
    }

}
