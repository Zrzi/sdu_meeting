package com.meeting.chatroom.handler;

import com.alibaba.fastjson.JSON;
import com.meeting.chatroom.entity.*;
import com.meeting.chatroom.service.ChatService;
import com.meeting.chatroom.service.FriendService;
import com.meeting.chatroom.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.*;

import java.util.HashMap;
import java.util.Map;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private Channel channel;

    private Long fromId;

    private final ChatService chatService = SpringUtil.getBean(ChatService.class);

    private final FriendService friendService = SpringUtil.getBean(FriendService.class);

    /**
     * 活跃的通道  也可以当作用户连接上客户端进行使用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 出现异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo 异常处理
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 不活跃的通道  就说明用户失去连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 从channelGroup通道组中移除
        channel = ctx.channel();
        ChatChannelGroup.LOCK.writeLock().lock();
        try {
            Long id = ChatChannelGroup.CHANNEL_ID_REF.get(channel);
            ChatChannelGroup.ID_CHANNEL_REF.remove(id);
            ChatChannelGroup.CHANNEL_ID_REF.remove(channel);
        } finally {
            ChatChannelGroup.LOCK.writeLock().unlock();
        }
    }

    /**
     * 服务器接受客户端的数据信息
     * @param ctx
     * @param data
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame data) {
        if (this.fromId == null) {
            this.channel = ctx.channel();
            ChatChannelGroup.LOCK.readLock().lock();
            try {
                this.fromId = ChatChannelGroup.CHANNEL_ID_REF.get(channel);
            } finally {
                ChatChannelGroup.LOCK.readLock().unlock();
            }
        }

        // 获取客户端发送的消息
        String content = data.text();
        MessageVO messageVO;
        try {
            messageVO = JSON.parseObject(content, MessageVO.class);
        } catch (RuntimeException exception) {
            ResponseData toSender = ResponseData.ILLEGAL_MESSAGE_FORMAT;
            this.channel.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
            return;
        }
        int type = messageVO.getType();

        // 根据type处理不同业务
        if (type == MessageType.CHAT.getType()) {
            // 聊天类型的消息
            MessageDO messageDO = new MessageDO(messageVO, this.fromId);
            // 发送消息
            // 从全channelMap中获取接受方的channel
            Channel sender = this.channel;
            Channel receiver = null;
            ChatChannelGroup.LOCK.readLock().lock();
            try {
                receiver = ChatChannelGroup.ID_CHANNEL_REF.get(messageDO.getToId());
            } finally {
                ChatChannelGroup.LOCK.readLock().unlock();
            }
            if (receiver == null) {
                // 对方离线
                sendToOfflineUser(sender, messageDO);
            } else {
                sendToOnlineUser(sender, receiver, messageDO);
            }
        } else if (type == MessageType.SIGNED.getType()) {
            // 消息签收
            if (messageVO.getId() == null) {
                this.channel.writeAndFlush(
                        new TextWebSocketFrame(JSON.toJSONString(ResponseData.ID_NOT_FOUND))
                );
            }
            sign(channel, messageVO);
        } else if (type == MessageType.REQUEST.getType()) {
            // 好友添加请求
            if (messageVO.getToId() == null) {
                this.channel.writeAndFlush(
                        new TextWebSocketFrame(JSON.toJSONString(ResponseData.ID_NOT_FOUND))
                );
            } else {
                handleRequest(fromId, messageVO.getToId());
            }
        } else if (type == MessageType.REPLY.getType()) {
            // 回复好友请求
            Long id = messageVO.getId();
            if (id == null) {
                this.channel.writeAndFlush(
                        new TextWebSocketFrame(JSON.toJSONString(ResponseData.ID_NOT_FOUND))
                );
            } else {
                handleReply(id, messageVO.isAgree());
            }
        } else if (type == MessageType.PULL_FRIENDS.getType()) {
            // 获取好友信息
            pullFriends(this.fromId);
        } else if (type == MessageType.PULL_REQUESTS.getType()) {
            // 获取好友请求
            pullRequests(this.fromId);
        } else if (type == MessageType.PULL_UNSIGNED_MESSAGE.getType()) {
            // 获取未签收消息
            handlePullUnsignedMessage(this.fromId);
        } else if (type == MessageType.PULL_HISTORY_MESSAGE.getType()) {
            // 获取历史消息记录
            if (messageVO.getToId() == null) {
                // toId未空
                this.channel.writeAndFlush(
                        new TextWebSocketFrame(JSON.toJSONString(ResponseData.ID_NOT_FOUND))
                );
            } else {
                int start = 0;
                if (messageVO.getStart() != null && messageVO.getStart() > 0) {
                    start = messageVO.getStart();
                }
                int num = 10;
                if (messageVO.getNum() != null && messageVO.getNum() > 0) {
                    num = messageVO.getNum();
                }
                handlePullHistoryMessage(fromId, messageVO.getToId(), start, num);
            }
        } else {
            handleDefault(this.channel);
        }
    }

    /**
     * 发送消息给在线用户
     * @param sender Channel对象 发送方
     * @param receiver Channel对象 接收方
     * @param message MessageDO对象 封装消息
     */
    private void sendToOnlineUser(Channel sender, Channel receiver, MessageDO message) {
        ResponseDataContainer container = chatService.sendToUser(message, true);
        ResponseData toSender = container.getToSender();
        ResponseData toReceiver = container.getToReceiver();
        if (!toSender.isSuccess()) {
            // success == false
            sender.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        } else {
            // 发送消息给接收方
            receiver.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toReceiver))
            );

            // 发送响应给发送方
            sender.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        }
    }

    /**
     * 发送消息给离线用户
     * @param sender Channel对象 发送方
     * @param message MessageDO对象 封装消息
     */
    private void sendToOfflineUser(Channel sender, MessageDO message) {
        ResponseDataContainer container = chatService.sendToUser(message, false);
        ResponseData toSender = container.getToSender();
        if (!toSender.isSuccess()) {
            // success == false
            sender.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        } else {
            // 发送响应给发送方
            sender.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        }
    }

    /**
     * 接收方签收消息
     * @param channel Channel对象 接收方
     * @param message MessageVO对象 封装消息Id
     */
    private void sign(Channel channel, MessageVO message) {
        ResponseDataContainer container = chatService.sign(message.getId(), this.fromId);
        channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(container.getToReceiver()))
        );
    }

    /**
     * 处理添加好友
     * @param fromId 发送方id
     * @param toId 接受方id
     */
    private void handleRequest(Long fromId, Long toId) {
        ResponseDataContainer container = friendService.requestFriend(fromId, toId);
        ResponseData toSender = container.getToSender();
        ResponseData toReceiver = container.getToReceiver();
        if (toSender.isSuccess()) {
            // 请求成功
            if (toSender != ResponseData.HAVE_ALREADY_REQUESTED) {
                // 不是重复请求
                Channel receiver = null;
                ChatChannelGroup.LOCK.readLock().lock();
                try {
                    receiver = ChatChannelGroup.ID_CHANNEL_REF.get(toId);
                } finally {
                    ChatChannelGroup.LOCK.readLock().unlock();
                }
                if (receiver != null) {
                    // 对方在线
                    receiver.writeAndFlush(
                            new TextWebSocketFrame(JSON.toJSONString(toReceiver))
                    );
                }
            }
            this.channel.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        } else {
            this.channel.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        }
    }

    /**
     * todo 代码结构需要修改
     * 处理回复好友请求
     * @param id 请求id
     * @param agree 是否同意
     */
    private void handleReply(Long id, boolean agree) {
        ResponseDataContainer container = friendService.replyFriend(id, agree, this.fromId);
        ResponseData toSender = container.getToSender();
        ResponseData toReceiver = container.getToReceiver();

        if (toSender.isSuccess()) {
            if (agree) {
                this.channel.writeAndFlush(
                        new TextWebSocketFrame(JSON.toJSONString(toSender))
                );
                if (toReceiver != null) {
                    ChatChannelGroup.LOCK.readLock().lock();
                    Channel receiver = null;
                    try {
                        Long receiverId = (Long) toSender.getData().get("id");
                        receiver = ChatChannelGroup.ID_CHANNEL_REF.get(receiverId);
                    } finally {
                        ChatChannelGroup.LOCK.readLock().unlock();
                    }
                    if (receiver != null) {
                        receiver.writeAndFlush(
                                new TextWebSocketFrame(JSON.toJSONString(toReceiver))
                        );
                    }
                }
            } else {
                this.channel.writeAndFlush(
                        new TextWebSocketFrame(JSON.toJSONString(toSender))
                );
            }
        } else {
            this.channel.writeAndFlush(
                    new TextWebSocketFrame(JSON.toJSONString(toSender))
            );
        }
    }

    /**
     * 获取好友信息
     * @param uid 用户id
     */
    private void pullFriends(Long uid) {
        ResponseDataContainer container = friendService.findFriends(uid);
        this.channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(container.getToSender()))
        );
    }

    /**
     * 获取好友请求
     * @param uid 用户id
     */
    private void pullRequests(Long uid) {
        ResponseDataContainer container = friendService.getRequest(uid);
        this.channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(container.getToReceiver()))
        );
    }

    /**
     * 获取未签收消息
     * @param uid 用户id
     */
    private void handlePullUnsignedMessage(long uid) {
        ResponseDataContainer container = chatService.selectUnsignedMessage(uid);
        this.channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(container.getToReceiver()))
        );
    }

    /**
     * 获取历史记录，包括未签收的
     * @param uid1 用户1的id
     * @param uid2 用户2的id
     * @param start 起始缩影
     * @param num 总记录数目
     */
    private void handlePullHistoryMessage(long uid1, long uid2, int start, int num) {
        ResponseDataContainer container = chatService.selectHistoryMessage(uid1, uid2, start, num);
        this.channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(container.getToReceiver()))
        );
    }

    /**
     * 默认处理位置消息类型
     * @param channel
     */
    private void handleDefault(Channel channel) {
        ResponseData responseData = ResponseData.TYPE_NOT_ALLOWED;
        channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(responseData))
        );
    }

}
