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
                                          @RequestParam(value = "messageId", required = false) Long start) {
        Long id = null;
        if (token == null || !jwtTokenUtil.validateToken(token)
                || (id = jwtTokenUtil.getUserIdFromToken(token)) == null) {
            throw new UnAuthorizedException();
        }
        if (start == null || start <= 0) {
            start = Long.MAX_VALUE;
        }
        int num = 5;
        return chatService.selectHistoryMessage(id, uid, start, num);
    }

}
