package com.meeting.chatroom.service;

import com.meeting.chatroom.entity.*;
import com.meeting.chatroom.mapper.MessageMapper;
import com.meeting.chatroom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                // 如果在线，默认用户签收消息，否则认为消息没有签收
                message.setStatus(isOnline ? 1 : 0);
                messageMapper.insertMessage(message);
                toSender = ResponseData.ok(ResponseType.MESSAGE_SENDER_OK.getType());
                toSender.getData().put("id", message.getId());
                if (isOnline) {
                    toReceiver= ResponseData.ok(ResponseType.MESSAGE_RECEIVER_OK.getType());
                    toReceiver.getData().put("message", message);
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
        List<MessageDO> list = messageMapper.findMessageByToIdAndStatus(uid, 0);
        toReceiver = ResponseData.ok(ResponseType.UNSIGNED_MESSAGE.getType());
        toReceiver.getData().put("list", list);
        container.setToReceiver(toReceiver);
        return container;
    }

    public ResponseDataContainer selectHistoryMessage(long uid1, long uid2, int start, int num) {
        ResponseDataContainer container = new ResponseDataContainer();
        ResponseData toReceiver = null;
        List<MessageDO> message = messageMapper.findHistoryMessage(uid1, uid2, start, num);
        toReceiver = ResponseData.ok(ResponseType.HISTORY_MESSAGE.getType());
        toReceiver.getData().put("list", message);
        container.setToReceiver(toReceiver);
        return container;
    }

}
