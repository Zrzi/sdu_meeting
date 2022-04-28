package com.meeting.chatroom.controller;

import com.meeting.chatroom.service.ChatService;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.exception.UnAuthorizedException;
import com.meeting.common.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = ("*"))
public class HistoryController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ChatService chatService;

    @ResponseBody
    @GetMapping("/getHistoryMessage")
    public ResponseData getHistoryMessage(@RequestHeader(value = "Authorization", required = false) String token,
                                          @RequestParam("toId") long uid,
                                          @RequestParam(value = "page", required = false) Integer page) {
        Long id = null;
        if (token == null || jwtTokenUtil.validateToken(token)
                || (id = jwtTokenUtil.getUserIdFromToken(token)) == null) {
            throw new UnAuthorizedException();
        }
        if (page == null || page < 0) {
            page = 0;
        }
        int num = 10;
        int start = num * (page - 1);
        return chatService.selectHistoryMessage(id, uid, start, num);
    }

}
