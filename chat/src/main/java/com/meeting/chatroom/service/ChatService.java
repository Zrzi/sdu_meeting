package com.meeting.chatroom.service;

import com.meeting.chatroom.entity.MessageDO;
import com.meeting.chatroom.entity.MessageVO;
import com.meeting.chatroom.entity.ResponseData;
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
    public ResponseData sendToUser(MessageDO message, boolean isOnline) {
        ResponseData responseData;
        if (message.getMessage().length() > 256) {
            responseData = ResponseData.MESSAGE_TOO_LONG;
        } else {
            if (userMapper.findUserById(message.getFromId()) == null
                    || userMapper.findUserById(message.getToId()) == null) {
                responseData = ResponseData.USER_ID_NOT_FOUND;
            } else {
                // 如果在线，默认用户签收消息，否则认为消息没有签收
                message.setStatus(isOnline ? 1 : 0);
                messageMapper.insertMessage(message);
                responseData = ResponseData.ok();
                responseData.getData().put("message", new MessageVO(message, 1));
            }
        }
        return responseData;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseData sign(long id, long toId) {
        ResponseData responseData;
        MessageDO message = messageMapper.findMessageById(id);
        if (message == null || message.getToId() != toId) {
            responseData = ResponseData.MESSAGE_NOT_EXIST;
        } else {
            message.setStatus(1);
            messageMapper.updateMessage(message);
            responseData = ResponseData.ok();
        }
        return responseData;
    }

    public ResponseData selectUnsignedMessage(long uid) {
        List<MessageDO> list = messageMapper.findMessageByToIdAndStatus(uid, 0);
        ResponseData responseData = ResponseData.ok();
        responseData.getData().put("list", list);
        return responseData;
    }

    public ResponseData selectHistoryMessage(long uid1, long uid2, int start, int num) {
        List<MessageDO> message = messageMapper.findHistoryMessage(uid1, uid2, start, num);
        ResponseData responseData = ResponseData.ok();
        responseData.getData().put("list", message);
        return responseData;
    }

}
