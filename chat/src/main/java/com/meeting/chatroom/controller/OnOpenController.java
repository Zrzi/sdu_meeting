package com.meeting.chatroom.controller;

import com.meeting.chatroom.entity.ChatChannelGroup;

import com.meeting.chatroom.service.OnOpenService;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 负责处理websocket连接后的一些请求
 */
@Controller
public class OnOpenController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private OnOpenService onOpenService;

    /**
     * 获取好友列表、好有请求、未签收的信息
     * @param token jwt token
     * @return ResponseData对象
     */
    @ResponseBody
    @GetMapping("/getFriendsAndMessages")
    public ResponseData getFriendsAndMessages(@RequestHeader(value = "Authorization", required = false) String token) {
        ResponseData responseData = null;
        Long uid = null;
        if (token == null || jwtTokenUtil.validateToken(token)
                || (uid = jwtTokenUtil.getUserIdFromToken(token)) == null) {
            return new ResponseData(401, "未登录");
        }
        return onOpenService.getFriendsAndMessage(uid);
    }

}
