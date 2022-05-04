package com.meeting.login_and_register.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.User;
import com.meeting.common.exception.*;
import com.meeting.login_and_register.service.UserService;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.common.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final static String[] EMAIL_PATTERN = new String[]{"@mail.sdu.edu.cn", "@sdu.edu.cn"};

    @ResponseBody
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping(value = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData code(@RequestParam("username") String username,
                             @RequestParam("email") String email) {
        ResponseData responseData;
        if (isEmptyString(username) || isEmptyString(email)) {
            responseData = new ResponseData(4001, "输入不能为空");
            return responseData;
        }
        String password = "111111";
        if (!checkValidLength(username, password, email)) {
            responseData = new ResponseData(4002, "长度不符合要求");
            return responseData;
        }
        if (!checkValidEmail(email)) {
            responseData = new ResponseData(4003, "不支持的邮件格式");
            return responseData;
        }
        try {
            userService.code(username, password, email);
            responseData = new ResponseData();
            responseData.setCode(200);
            responseData.setMessage("ok");
            return responseData;
        } catch (IllegalUsernameException exception) {
            responseData = new ResponseData(4004, exception.getMsg());
            return responseData;
        } catch (UserExistException exception) {
            responseData = new ResponseData(4004, exception.getMsg());
            return responseData;
        } catch (BaseException exception) {
            responseData = new ResponseData(500, exception.getMsg());
            return responseData;
        }
    }

    @ResponseBody
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData register(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("email") String email,
                                 @RequestParam("code") String code) {
        ResponseData responseData;
        if (isEmptyString(username) || isEmptyString(email) || isEmptyString(password)) {
            responseData = new ResponseData(4001, "输入不能为空");
            return responseData;
        }
        if (!checkValidLength(username, password, email)) {
            responseData = new ResponseData(4002, "长度不符合要求");
            return responseData;
        }
        if (!checkValidEmail(email)) {
            responseData = new ResponseData(4003, "不支持的邮件格式");
            return responseData;
        }
        try {
            Long uid = userService.register(username, password, email, code);
            responseData = new ResponseData(200, "ok");
            responseData.getData().put("uid", uid);
            return responseData;
        } catch (CodeNotFoundException exception) {
            responseData = new ResponseData(4004, exception.getMsg());
            return responseData;
        } catch (UsernameNotFoundException exception) {
            responseData = new ResponseData(4004, exception.getMsg());
            return responseData;
        } catch (EmailNotFoundException exception) {
            responseData = new ResponseData(4004, exception.getMsg());
            return responseData;
        }
    }

    /**
     * 根据用户名查询相似用户，相似查询
     * @param token jwt token
     * @param username 用户名
     * @return ResponseData对象
     */
    @ResponseBody
    @GetMapping(value = "/findUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData findUserByName(@RequestHeader("Authorization") String token,
                                       @RequestParam("name") String username) {

        if (token == null || !jwtTokenUtil.validateToken(token)) {
            throw new UnAuthorizedException();
        }
        final Long uid = jwtTokenUtil.getUserIdFromToken(token);
        if (uid == null) {
            throw new UnAuthorizedException();
        }
        if (username == null || username.length() == 0) {
            return new ResponseData(4001, "输入不能为空");
        }
        ResponseData responseData = new ResponseData(200, "ok");
        List<Map<String, Object>> users =
                userService.findUserByName(username)
                        .stream()
                        .filter((user) -> !user.getId().equals(uid))
                        .map(User::toMap)
                        .collect(Collectors.toList());
        responseData.getData().put("users", users);
        return responseData;
    }

    private boolean isEmptyString(String string) {
        return string == null || "".equals(string);
    }

    private boolean checkValidLength(String username, String password, String email) {
        return username.length() <= 32 && password.length() <= 32 && email.length() <= 64;
    }

    private boolean checkValidEmail(String email) {
        for (String pattern : EMAIL_PATTERN) {
            if (email.endsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

}
