package com.meeting.file.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.exception.FileFormatException;
import com.meeting.common.exception.UnAuthorizedException;
import com.meeting.common.exception.UserExistException;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.file.service.UserService;
import com.meeting.file.util.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
@CrossOrigin(origins = {"*"})
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @ResponseBody
    @PostMapping("/updateUserProfile")
    public ResponseData updateUserProfile(@RequestHeader("Authorization") String token,
                                          @RequestParam("img") MultipartFile img) {
        ResponseData responseData;
        Long uid;
        if (token == null || !jwtTokenUtil.validateToken(token)
                || (uid = jwtTokenUtil.getUserIdFromToken(token)) == null) {
            throw new UnAuthorizedException();
        }
        try {
            pictureUtil.handlePicture("user", img, uid);
            if (userService.updateUserProfile(uid)) {
                // 修改成功
                responseData = new ResponseData(200, "ok");
                return responseData;
            } else {
                // 修改记录为0
                responseData = new ResponseData(500, "文件保存错误");
                return responseData;
            }
        } catch (FileFormatException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        } catch (IOException exception) {
            responseData = new ResponseData(500, "文件传输错误");
            return responseData;
        } catch (UserExistException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        } catch (IllegalStateException exception) {
            responseData = new ResponseData(400, exception.getMessage());
            return responseData;
        }
    }
    
    @ResponseBody
    @GetMapping("/pic/user/{filename}")
    public byte[] getUserProfile(@PathVariable("filename") String filename)
            throws FileNotFoundException {
        return pictureUtil.openPicture(filename, "user");
    }

}
