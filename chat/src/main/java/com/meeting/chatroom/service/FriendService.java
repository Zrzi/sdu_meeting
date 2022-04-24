package com.meeting.chatroom.service;

import com.meeting.chatroom.entity.*;
import com.meeting.chatroom.mapper.FriendMapper;
import com.meeting.chatroom.mapper.MessageMapper;
import com.meeting.chatroom.mapper.UserMapper;
import com.meeting.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ResponseDataContainer findFriends(Long userId) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toSender = null;
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
        toSender = ResponseData.ok(ResponseType.FRIEND.getType());
        toSender.getData().put("list", collect);
        container.setToSender(toSender);
        return container;
    }

    public ResponseDataContainer requestFriend(Long userId, Long friendId) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toSender = null;
        ResponseData toReceiver = null;

        User sender = null;
        if ((sender = userMapper.findUserById(userId)) == null
                || userMapper.findUserById(friendId) == null) {
            // 一方用户不存在
            toSender = ResponseData.USER_ID_NOT_FOUND;
            container.setToSender(toSender);
            return container;
        }

        if (messageMapper.findRequestByFromIdAndToId(userId, friendId) != null) {
            // 已经发送过请求
            toSender = ResponseData.HAVE_ALREADY_REQUESTED;
            container.setToSender(toSender);
            return container;
        }

        Friend f1 = null;
        Friend f2 = null;
        if ((f1 = friendMapper.findFriendByUserId(userId, friendId)) != null
                | (f2 = friendMapper.findFriendByUserId(friendId, userId)) != null) {
            // 已经是好友，至多有一个是null
            if (f1 == null) {
                f1 = new Friend();
                f1.setUid(userId);
                f1.setFriendId(friendId);
                friendMapper.insertFriend(f1);
            }
            if (f2 == null) {
                f2 = new Friend();
                f2.setUid(friendId);
                f2.setFriendId(userId);
                friendMapper.insertFriend(f2);
            }
            toSender = ResponseData.IS_ALREADY_FRIEND;
            container.setToSender(toSender);
            return container;
        }

        // 添加好友请求
        MessageDO message = new MessageDO();
        message.setFromId(userId);
        message.setToId(friendId);
        message.setDate(System.currentTimeMillis());
        message.setStatus(2);
        messageMapper.insertMessage(message);

        // 返回给发送方的响应
        toSender = ResponseData.ok(ResponseType.REQUEST_SENDER_OK.getType());
        toSender.getData().put("id", message.getId());
        container.setToSender(toSender);

        // 返回给接收方的响应
        toReceiver = ResponseData.ok(ResponseType.REQUEST_RECEIVER_OK.getType());
        toReceiver.getData().put("id", message.getId());
        toReceiver.getData().put("userId", sender.getId());
        toReceiver.getData().put("username", sender.getUsername());
        toReceiver.getData().put("email", sender.getEmail());
        toReceiver.getData().put("profile", sender.getProfile());
        toReceiver.getData().put("date", message.getDate());
        container.setToReceiver(toReceiver);

        return container;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDataContainer replyFriend(Long id, boolean agree, Long toId) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toSender = null;
        ResponseData toReceiver = null;

        MessageDO message = messageMapper.findMessageById(id);
        if (message == null || message.getToId() != toId) {
            toSender = ResponseData.MESSAGE_NOT_EXIST;
            container.setToSender(toSender);
            return container;
        }

        User sender = userMapper.findUserById(message.getToId());
        User receiver = userMapper.findUserById(message.getFromId());

        if (!sender.getId().equals(toId)) {
            // 不匹配
            toSender = ResponseData.MESSAGE_NOT_EXIST;
            container.setToSender(toSender);
            return container;
        }

        message.setStatus(3);
        messageMapper.updateMessage(message);

        if (agree) {
            // 当前用户同意请求
            Friend f1 = friendMapper.findFriendByUserId(sender.getId(), receiver.getId());
            Friend f2 = friendMapper.findFriendByUserId(receiver.getId(), sender.getId());

            toSender = ResponseData.IS_ALREADY_FRIEND;

            if (f1 != null && f2 != null) {
                // f1 和 f2 都不为空，存在记录
                container.setToSender(toSender);
                container.setToReceiver(toReceiver);
                return container;
            }

            // f1 和 f2 至少一个是null
            if (f1 == null) {
                // 关系缺失，添加
                f1 = new Friend();
                f1.setUid(sender.getId());
                f1.setFriendId(receiver.getId());
                friendMapper.insertFriend(f1);
                // 返回当前用户的消息
                toSender = ResponseData.ok(ResponseType.REPLY_SENDER_OK.getType());
                toSender.getData().put("id", receiver.getId());
                toSender.getData().put("username", receiver.getUsername());
                toSender.getData().put("email", receiver.getEmail());
                toSender.getData().put("profile", receiver.getProfile());
            }
            if (f2 == null) {
                // 关系缺失，添加
                f2 = new Friend();
                f2.setUid(receiver.getId());
                f2.setFriendId(sender.getId());
                friendMapper.insertFriend(f2);
                // 返回发送好友请求的用户的消息
                toReceiver = ResponseData.ok(ResponseType.REPLY_RECEIVER_OK.getType());
                toSender.getData().put("id", receiver.getId());
                toReceiver.getData().put("id", sender.getId());
                toReceiver.getData().put("username", sender.getUsername());
                toReceiver.getData().put("email", sender.getEmail());
                toReceiver.getData().put("profile", sender.getProfile());
            }

            container.setToSender(toSender);
            container.setToReceiver(toReceiver);
            return container;
        } else {
            // 当前用户拒绝请求
            toSender = ResponseData.ok(ResponseType.REPLY_SENDER_OK.getType());
            container.setToSender(toSender);
            return container;
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDataContainer getRequest(Long userId) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toReceiver = null;
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
                            map.put("date", request.getDate());
                            return map;
                        })
                        .collect(Collectors.toList());
        toReceiver = ResponseData.ok(ResponseType.REQUESTS_TO_BE_REPLIED.getType());
        toReceiver.getData().put("list", collect);
        container.setToReceiver(toReceiver);
        return container;
    }

}
