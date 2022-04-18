package com.meeting.file.controller;

import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.User;
import com.meeting.common.exception.BaseException;
import com.meeting.common.exception.FileFormatException;
import com.meeting.common.exception.UnAuthorizedException;
import com.meeting.common.exception.UserExistException;
import com.meeting.common.util.JwtTokenUtil;
import com.meeting.file.service.UserService;
import com.meeting.file.util.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    @PostMapping(value = "/updateUserProfile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData updateUserProfile(@RequestHeader("Authorization") String token,
                                          @RequestParam("img") MultipartFile img,
                                          @RequestParam("fileType") String type) {
        ResponseData responseData;
        Long uid;
        if (token == null || !jwtTokenUtil.validateToken(token)
                || (uid = jwtTokenUtil.getUserIdFromToken(token)) == null) {
            throw new UnAuthorizedException();
        }
        try {
            User user = userService.findUserByUid(uid);
            if (userService.updateUserProfile(img, user, type)) {
                // 修改成功
                responseData = new ResponseData(200, "ok");
                Map<String, Object> info = new HashMap<String, Object>();
                info.put("profile", type);
                responseData.getData().put("token", jwtTokenUtil.refreshToken(token, info));
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
        } catch (BaseException exception) {
            responseData = new ResponseData(400, exception.getMsg());
            return responseData;
        }
    }

    @ResponseBody
    @GetMapping(value = "/pic/user/{filename}.jpeg", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getUserProfileJpeg(@PathVariable("filename") String filename)
            throws FileNotFoundException {
        return pictureUtil.openPicture(filename + ".jpeg", "user");
    }

    @ResponseBody
    @GetMapping(value = "/pic/user/{filename}.png", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getUserProfilePng(@PathVariable("filename") String filename)
            throws FileNotFoundException {
        return pictureUtil.openPicture(filename + ".png", "user");
    }

}
