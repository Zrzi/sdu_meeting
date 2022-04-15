package com.meeting.login_and_register.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.User;
import com.meeting.common.exception.*;
import com.meeting.login_and_register.service.UserService;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.common.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = {"*"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Md5Util md5Util;

    /**
     * todo 正则表达式 @mail.sdu.edu.cn @sdu.edu.cn
     */
    private final static String EMAIL_PATTERN = "@mail.sdu.edu.cn";

    @ResponseBody
    @PostMapping("/login")
    public ResponseData login(@RequestParam("text") String text,
                              @RequestParam("password") String password) {
        ResponseData responseData;
        User user;
        if (checkValidEmail(text)) {
            user = userService.loadUserByEmail(text);
        } else {
            user = userService.loadUserByUsername(text);
        }
        if (user != null) {
            if (md5Util.verify(password, user.getPassword())) {
                responseData = new ResponseData(200, "ok");
                responseData.getData().put("token", jwtTokenUtil.generateToken(user));
            } else {
                responseData = new ResponseData(400, "密码错误");
            }
        } else {
            responseData = new ResponseData(400, "用户名或者邮箱不存在");
        }
        return responseData;
    }

    @ResponseBody
    @PostMapping("/code")
    public ResponseData code(@RequestParam("username") String username,
                             @RequestParam("email") String email) {
        ResponseData responseData;
        if (isStringEmpty(username) || isStringEmpty(email)) {
            responseData = new ResponseData(400, "不能为空");
            return responseData;
        }
        String password = "111111";
        if (!checkValidLength(username, password, email)) {
            responseData = new ResponseData(400, "长度问题");
            return responseData;
        }
        if (!checkValidEmail(email)) {
            responseData = new ResponseData(400, "不支持的邮件格式");
            return responseData;
        }
        try {
            userService.code(username, password, email);
            responseData = new ResponseData();
            responseData.setCode(200);
            responseData.setMessage("ok");
            return responseData;
        } catch (IllegalUsernameException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        } catch (UserExistException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        } catch (RuntimeException exception) {
            responseData = new ResponseData(400, exception.getMessage());
            return responseData;
        }
    }

    @ResponseBody
    @PostMapping("/register")
    public ResponseData register(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("email") String email,
                                 @RequestParam("code") String code) {
        ResponseData responseData;
        if (isStringEmpty(username) || isStringEmpty(email)) {
            responseData = new ResponseData(400, "不能为空");
            return responseData;
        }
        if (!checkValidLength(username, password, email)) {
            responseData = new ResponseData(400, "长度问题");
            return responseData;
        }
        if (!checkValidEmail(email)) {
            responseData = new ResponseData(400, "不支持的邮件格式");
            return responseData;
        }
        try {
            Long uid = userService.register(username, password, email, code);
            responseData = new ResponseData(200, "ok");
            responseData.getData().put("uid", uid);
            return responseData;
        } catch (CodeNotFoundException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        } catch (UsernameNotFoundException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        } catch (EmailNotFoundException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        }
    }

    private boolean isStringEmpty(String string) {
        return string == null || "".equals(string);
    }

    private boolean checkValidLength(String username, String password, String email) {
        return username.length() <= 32 && password.length() <= 32 && email.length() <= 64;
    }

    private boolean checkValidEmail(String email) {
        return email.endsWith(EMAIL_PATTERN);
    }

}
