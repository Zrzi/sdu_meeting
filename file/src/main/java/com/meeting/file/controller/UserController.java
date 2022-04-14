package com.meeting.file.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.file.service.UserService;
import com.meeting.file.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileUtil fileUtil;

    public ResponseData updateUserProfile(@RequestParam("uid") Long uid,
                                          @RequestParam("img") MultipartFile img) {
        ResponseData responseData;
        try {
            String profile = fileUtil.handlePicture("user", img);
            if (userService.updateUserProfile(uid, profile)) {
                // 修改成功
                responseData = new ResponseData(200, "ok");
                return responseData;
            } else {
                // 修改记录为0
                responseData = new ResponseData(500, "文件保存错误");
                return responseData;
            }
        } catch (IOException exception) {
            responseData = new ResponseData(500, "文件传输错误");
            return responseData;
        }
    }

}
