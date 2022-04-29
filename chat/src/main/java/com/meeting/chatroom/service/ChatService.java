package com.meeting.chatroom.service;

import com.meeting.chatroom.entity.*;
import com.meeting.chatroom.mapper.MessageMapper;
import com.meeting.chatroom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDataContainer sendToUser(MessageDO message, boolean isOnline) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toSender = null;
        ResponseData toReceiver = null;
        if (message.getMessage().length() > 256) {
            toSender = ResponseData.MESSAGE_TOO_LONG;
        } else {
            if (userMapper.findUserById(message.getFromId()) == null
                    || userMapper.findUserById(message.getToId()) == null) {
                toSender = ResponseData.USER_ID_NOT_FOUND;
            } else {
                // 改为，统一默认用户没有接受
                message.setStatus(0);
                messageMapper.insertMessage(message);
                toSender = ResponseData.ok(ResponseType.MESSAGE_SENDER_OK.getType());
                toSender.getData().put("id", message.getId());
                if (isOnline) {
                    toReceiver= ResponseData.ok(ResponseType.MESSAGE_RECEIVER_OK.getType());
                    toReceiver.getData().put("message", message.toMap());
                }
            }
        }
        container.setToSender(toSender);
        container.setToReceiver(toReceiver);
        return container;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseDataContainer sign(long id, long toId) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toReceiver = null;
        MessageDO message = messageMapper.findMessageById(id);
        if (message == null || message.getToId() != toId) {
            toReceiver = ResponseData.MESSAGE_NOT_EXIST;
        } else {
            message.setStatus(1);
            messageMapper.updateMessage(message);
            toReceiver = ResponseData.ok(ResponseType.SIGN_OK.getType());
        }
        container.setToReceiver(toReceiver);
        return container;
    }

    public ResponseDataContainer selectUnsignedMessage(long uid) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toReceiver = null;
        List<Map<String, Object>> collect = messageMapper
                .findMessageByToIdAndStatus(uid, 0)
                .stream()
                .map(MessageDO::toMap)
                .collect(Collectors.toList());
        toReceiver = ResponseData.ok(ResponseType.UNSIGNED_MESSAGE.getType());
        toReceiver.getData().put("list", collect);
        container.setToReceiver(toReceiver);
        return container;
    }

    public com.meeting.common.entity.ResponseData selectHistoryMessage(long uid1, long uid2, int start, int num) {
        com.meeting.common.entity.ResponseData responseData =
                new com.meeting.common.entity.ResponseData();
        List<Map<String, Object>> collect = messageMapper
                .findHistoryMessage(uid1, uid2, start, num)
                .stream()
                .map(MessageDO::toMap)
                .collect(Collectors.toList());
        responseData.setCode(200);
        responseData.setMessage("ok");
        responseData.getData().put("list", collect);
        return responseData;
    }

}
