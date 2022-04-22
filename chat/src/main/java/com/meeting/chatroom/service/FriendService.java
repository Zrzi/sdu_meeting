package com.meeting.chatroom.service;

import com.meeting.chatroom.entity.Friend;
import com.meeting.chatroom.entity.MessageDO;
import com.meeting.chatroom.entity.ResponseData;
import com.meeting.chatroom.mapper.FriendMapper;
import com.meeting.chatroom.mapper.MessageMapper;
import com.meeting.chatroom.mapper.UserMapper;
import com.meeting.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseData findFriends(Long userId) {
        List<Map<String, Object>> collect =
                friendMapper
                        .findFriendsByUserId(userId)
                        .stream()
                        .map(userMapper::findUserById)
                        .map((user) -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", user.getId());
                            map.put("username", user.getUsername());
                            map.put("profile", user.getProfile());
                            map.put("email", user.getEmail());
                            return map;
                        })
                        .collect(Collectors.toList());
        ResponseData responseData = ResponseData.ok();
        responseData.getData().put("list", collect);
        return responseData;
    }

    public ResponseData requestFriend(Long userId, Long friendId) {
        if (userMapper.findUserById(userId) == null || userMapper.findUserById(friendId) == null) {
            return ResponseData.USER_ID_NOT_FOUND;
        }
        if (messageMapper.findRequestByFromIdAndToId(userId, friendId) != null) {
            return ResponseData.HAVE_ALREADY_REQUESTED;
        }
        MessageDO message = new MessageDO();
        message.setFromId(userId);
        message.setToId(friendId);
        message.setDate(System.currentTimeMillis());
        message.setStatus(2);
        messageMapper.insertMessage(message);
        ResponseData responseData = ResponseData.ok();
        responseData.getData().put("message", message);
        return responseData;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseData replyFriend(Long id, boolean agree, Long toId) {
        MessageDO message = messageMapper.findMessageById(id);
        if (message == null || message.getToId() != toId) {
            return ResponseData.MESSAGE_NOT_EXIST;
        }
        User from = userMapper.findUserById(message.getFromId());
        User to = userMapper.findUserById(message.getToId());
        if (to.getId().equals(toId)) {
            message.setStatus(3);
            messageMapper.updateMessage(message);
            if (agree) {
                if (!friendMapper.findFriendsByUserId(toId).contains(from.getId())) {
                    // to不存在from好友
                    Friend friend = new Friend(message.getFromId(), message.getToId());
                    friendMapper.insertFriend(friend);
                    friend = new Friend(message.getToId(), message.getFromId());
                    friendMapper.insertFriend(friend);
                }
                if (!friendMapper.findFriendsByUserId(from.getId()).contains(toId)) {
                    // from不存在to好友
                    Friend friend = new Friend(message.getFromId(), message.getToId());
                    friendMapper.insertFriend(friend);
                    friend = new Friend(message.getToId(), message.getFromId());
                    friendMapper.insertFriend(friend);
                }
            }
            ResponseData responseData = ResponseData.ok();
            responseData.getData().put("from", from);
            responseData.getData().put("to", to);
            return responseData;
        } else {
            // 不匹配
            return ResponseData.MESSAGE_NOT_EXIST;
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseData getRequest(Long userId) {
        List<Map<String, Object>> collect =
                messageMapper
                        .findMessageByToIdAndStatus(userId, 2)
                        .stream()
                        .map((request) -> {
                            Map<String, Object> map = new HashMap<>();
                            User from = userMapper.findUserById(request.getFromId());
                            map.put("id", request.getId());
                            map.put("uid", from.getId());
                            map.put("username", from.getUsername());
                            map.put("profile", from.getProfile());
                            map.put("email", from.getEmail());
                            map.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(request.getDate())));
                            return map;
                        })
                        .collect(Collectors.toList());
        ResponseData responseData = ResponseData.ok();
        responseData.getData().put("list", collect);
        return responseData;
    }

}
