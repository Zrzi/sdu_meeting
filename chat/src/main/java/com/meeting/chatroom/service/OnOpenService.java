package com.meeting.chatroom.service;

import com.meeting.chatroom.entity.MessageDO;
import com.meeting.chatroom.mapper.FriendMapper;
import com.meeting.chatroom.mapper.MessageMapper;
import com.meeting.chatroom.mapper.UserMapper;
import com.meeting.common.entity.ResponseData;
import com.meeting.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OnOpenService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseData getFriendsAndMessage(Long uid) {
        List<Map<String, Object>> friends = friendMapper
                .findFriendsByUserId(uid)
                .stream()
                .map(id -> userMapper.findUserById(id))
                .map(User::toMap)
                .collect(Collectors.toList());
        List<Map<String, Object>> messages = messageMapper
                .findMessageByToIdAndStatus(uid, 0)
                .stream()
                .map(MessageDO::toMap)
                .collect(Collectors.toList());
        List<Map<String, Object>> requests = messageMapper
                .findMessageByToIdAndStatus(uid, 2)
                .stream()
                .map(MessageDO::toMap)
                .collect(Collectors.toList());
        ResponseData responseData = new ResponseData(200, "ok");
        responseData.getData().put("friends", friends);
        responseData.getData().put("messages", messages);
        responseData.getData().put("requests", requests);
        return responseData;
    }

}
