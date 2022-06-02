package com.meeting.chatroom.handler;

import com.alibaba.fastjson.JSON;
import com.meeting.chatroom.entity.*;
import com.meeting.chatroom.service.ChatService;
import com.meeting.chatroom.service.FriendService;
import com.meeting.chatroom.util.SpringUtil;
import com.meeting.common.util.JwtTokenUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.UNAUTHORIZED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelPromise handshakePromise;

    private Channel channel = null;

    private Long fromId = null;

    private AtomicBoolean isFirst = new AtomicBoolean(true);

    private final JwtTokenUtil jwtTokenUtil = SpringUtil.getBean(JwtTokenUtil.class);

    private final ChatService chatService = SpringUtil.getBean(ChatService.class);

    private final FriendService friendService = SpringUtil.getBean(FriendService.class);

    private final ChatChannelGroup chatChannelGroup = SpringUtil.getBean(ChatChannelGroup.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakePromise = ctx.newPromise();
    }

    /**
     * 活跃的通道  也可以当作用户连接上客户端进行使用
     * @param ctx ChannelHandlerContext对象 channel上下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 出现异常
     * @param ctx ChannelHandlerContext对象 channel上下文
     * @param cause 异常信息
     * @throws Exception 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // todo 异常处理
        System.out.println(cause.getClass());
        System.out.println(cause.getMessage());
        sendMessageToChannel(ctx.channel(), ResponseData.SERVER_PROBLEM);
    }

    /**
     * 不活跃的通道  就说明用户失去连接
     * @param ctx ChannelHandlerContext对象 channel上下文
     * @throws Exception 异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 从channelGroup通道组中移除
        if (this.channel != null) {
            chatChannelGroup.removeChannel(this.channel);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
        } else {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                // 读写空闲，断开连接
                final Channel channel = ctx.channel();
                if (isFirst.getAndSet(false)) {
                    // 第一次读写超时，发送心跳包
                    sendMessageToChannel(channel, ResponseData.HEARTBEAT);
                } else {
                    // 第二次读写超时，断开连接
                    ChannelFuture close = ctx.close();
                    close.addListener(future -> chatChannelGroup.removeChannel(channel));
                }
            }
        }
    }

    /**
     * 服务器接受客户端的数据信息
     * @param ctx ChannelHandlerContext对象 channel上下文
     * @param data 数据
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object data) throws Exception {
        if (data instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) data);
        } else if (data instanceof TextWebSocketFrame) {
            isFirst.set(true);
            handleWebSocketFrame((TextWebSocketFrame) data);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!GET.equals(req.method())) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, ctx.alloc().buffer(0)));
            return;
        }

        Long fromId;
        Channel channel;

        String token = req.headers().get("Sec-WebSocket-Protocol");
        if (token == null || !jwtTokenUtil.validateToken(token)
                || (fromId = jwtTokenUtil.getUserIdFromToken(token)) == null) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, UNAUTHORIZED, ctx.alloc().buffer(0)));
            return;
        }

        channel = ctx.channel();
        if (this.fromId == null) {
            this.fromId = fromId;
        }
        if (this.channel == null) {
            this.channel = channel;
        }

        boolean success = this.chatChannelGroup.addChannel(fromId, channel);
        if (success) {
            // 添加成功
            final WebSocketServerHandshakerFactory wsFactory =
                    new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), req, "/ws"),
                            "WebSocket", true, 65536 * 10);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            final ChannelPromise localHandshakePromise = handshakePromise;
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                // setHandshaker(ctx.channel(), handshaker);
                HttpHeaders headers = new DefaultHttpHeaders();
                headers.set("Access-Control-Allow-Methods", "*");
                headers.set("Access-Control-Allow-Credentials", "true");
                headers.set("Access-Control-Allow-Origin", "*");
                headers.set("Access-Control-Allow-Headers", "*");
                headers.set("Access-Control-Expose-Headers", "*");
                headers.set("Sec-WebSocket-Protocol", token);
                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req, headers, ctx.newPromise());
                handshakeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (!future.isSuccess()) {
                            localHandshakePromise.tryFailure(future.cause());
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            localHandshakePromise.trySuccess();
                        }
                    }
                });
            }
        } else {
            // 已经连接过
            final Channel temp = this.chatChannelGroup.getChannelById(fromId);
            sendMessageToChannel(temp, ResponseData.NEW_CONNECTION)
                    .addListener(f -> {
                        if (f.isDone()) {
                            // 由前端接收到消息后主动断开连接
                            this.chatChannelGroup.removeChannel(temp);
                            handleHttpRequest(ctx, req);
                        }
                    });
//            com.meeting.common.entity.ResponseData responseData =
//                    new com.meeting.common.entity.ResponseData(400, "重复连接");
//            ByteBuf buf = Unpooled.wrappedBuffer(JSON.toJSONString(responseData).getBytes(StandardCharsets.UTF_8));
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN, buf);
//            response.headers().set("Content-Type","application/json;charset=UTF-8");
//            response.headers().set("Content-Length",response.content().readableBytes());
//            sendHttpResponse(ctx, req, response);
        }
    }

    private void handleWebSocketFrame(TextWebSocketFrame data) {
        // 获取客户端发送的消息
        String content = data.text();
        MessageVO messageVO;
        try {
            messageVO = JSON.parseObject(content, MessageVO.class);
        } catch (RuntimeException exception) {
            ResponseData toSender = ResponseData.ILLEGAL_MESSAGE_FORMAT;
            sendMessageToChannel(this.channel, toSender);
            return;
        }

        if (messageVO.getType() == null) {
            sendMessageToChannel(this.channel, ResponseData.ILLEGAL_MESSAGE_FORMAT);
        }

        int type = messageVO.getType();

        // 根据type处理不同业务
        if (type == MessageType.CHAT.getType()) {
            // type == 1
            // 聊天类型的消息
            MessageDO messageDO = new MessageDO(messageVO, this.fromId);
            // 发送消息
            // 从全channelMap中获取接受方的channel
            Channel sender = this.channel;
            Channel receiver = chatChannelGroup.getChannelById(messageVO.getToId());
            if (receiver == null) {
                // 对方离线
                sendToOfflineUser(sender, messageDO);
            } else {
                sendToOnlineUser(sender, receiver, messageDO);
            }
        } else if (type == MessageType.SIGNED.getType()) {
            // 消息签收
            // type == 2
            if (messageVO.getSender() == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            }
            sign(channel, messageVO);
        } else if (type == MessageType.REQUEST.getType()) {
            // type == 3
            // 好友添加请求
            if (messageVO.getToId() == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleRequest(fromId, messageVO.getToId());
            }
        } else if (type == MessageType.REPLY.getType()) {
            // type == 4
            // 回复好友请求
            Long id = messageVO.getId();
            if (id == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleReply(id, messageVO.isAgree());
            }
        } else if (type == MessageType.PRIVATE_WEBRTC_OFFER.getType()) {
            // type == 5
            // 用户发起会话请求
            Long sender = messageVO.getSender();
            Long receiver = messageVO.getReceiver();
            if (sender == null || receiver == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleWebRtcOffer(messageVO, receiver, sender);
            }
        } else if (type == MessageType.PRIVATE_WEBRTC_ANSWER.getType()) {
            // type == 6
            // 响应会话请求
            Long sender = messageVO.getSender();
            Long receiver = messageVO.getReceiver();
            if (sender == null || receiver == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleWebRtcAnswer(messageVO, sender);
            }
        } else if (type == MessageType.PRIVATE_WEBRTC_CANDIDATE.getType()) {
            // type == 7
            // ICE候选者
            Long target = messageVO.getTarget();
            if (target == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleWebRtcCandidate(messageVO, target);
            }
        } else if (type == MessageType.PRIVATE_WEBRTC_DISCONNECT.getType()) {
            // type == 8
            // 挂断电话
            Long target = messageVO.getTarget();
            if (target == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleWebRtcDisconnect(messageVO, target);
            }
        } else if (type == MessageType.REQUEST_WEBRTC.getType()) {
            // type == 9
            Long sender = messageVO.getSender();
            Long receiver = messageVO.getReceiver();
            if (sender == null || receiver == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else {
                handleRequestWebRtc(messageVO, receiver, sender);
            }
        } else if (type == MessageType.ANSWER_WEBRTC.getType()) {
            // type == 10
            Long sender = messageVO.getSender();
            Long receiver = messageVO.getReceiver();
            Integer accept = messageVO.getAccept();
            if (sender == null || receiver == null) {
                sendMessageToChannel(this.channel, ResponseData.ID_NOT_FOUND);
            } else if (accept == null) {
                sendMessageToChannel(this.channel, ResponseData.ILLEGAL_MESSAGE_FORMAT);
            } else {
                handleAnswerWebTrc(messageVO, sender);
            }
        } else if (type == MessageType.HEARTBEAT.getType()) {
            // type == 11
            // 什么都不做
        } else {
            // 以上都不匹配
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
        // 发送响应给发送方
        sendMessageToChannel(sender, toSender);
        if (toSender.isSuccess()) {
            // 发送消息给接收方
            sendMessageToChannel(receiver, toReceiver);
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
        sendMessageToChannel(sender, toSender);
    }

    /**
     * 接收方签收消息
     * @param channel Channel对象 接收方
     * @param message MessageVO对象 封装消息Id
     */
    private void sign(Channel channel, MessageVO message) {
        ResponseDataContainer container = chatService.sign(message.getSender(), this.fromId);
        sendMessageToChannel(channel, container.getToReceiver());
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
                Channel receiver = chatChannelGroup.getChannelById(toId);
                if (receiver != null) {
                    // 对方在线
                    sendMessageToChannel(receiver, toReceiver);
                }
            }
            sendMessageToChannel(this.channel, toSender);
        } else {
            sendMessageToChannel(this.channel, toSender);
        }
    }

    /**
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
                sendMessageToChannel(this.channel, toSender);
                if (toReceiver != null) {
                    Long receiverId = (Long) toSender.getData().get("id");
                    Channel receiver = chatChannelGroup.getChannelById(receiverId);
                    if (receiver != null) {
                        sendMessageToChannel(receiver, toReceiver);
                    }
                }
            } else {
                sendMessageToChannel(this.channel, toSender);
            }
        } else {
            sendMessageToChannel(this.channel, toSender);
        }
    }

    /**
     * 处理用户发起会话请求
     * @param messageVO messageVO对象，封装消息
     * @param receiver 接收方的id
     */
    private void handleWebRtcOffer(MessageVO messageVO, Long receiver, Long sender) {
        // 因为是发起请求，因此服务器应将该消息从 sender 向 receiver 转发
        Channel receiverChannel = chatChannelGroup.getChannelById(receiver);
        Channel senderChannel = chatChannelGroup.getChannelById(sender);
        Map<String, Object> map = new HashMap<>(8);
        if (receiverChannel != null) {
            map.put("type", messageVO.getType());
            map.put("sdp", messageVO.getSdp());
            map.put("sender", messageVO.getSender());
            map.put("receiver", messageVO.getReceiver());
            sendMessageToChannel(receiverChannel, map);
        } else {
            map.put("accept", -2);
            sendMessageToChannel(senderChannel, map);
        }
    }

    /**
     * 处理响应会话请求
     * @param messageVO messageVO对象，封装消息
     * @param sender 发送方用户id
     */
    private void handleWebRtcAnswer(MessageVO messageVO, Long sender) {
        // 因为是响应对方发起的请求，因此服务器应将该消息从 receiver 向 sender 转发。
        Channel senderChannel = chatChannelGroup.getChannelById(sender);
        if (senderChannel != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", messageVO.getType());
            map.put("sdp", messageVO.getSdp());
            map.put("sender", messageVO.getSender());
            map.put("receiver", messageVO.getReceiver());
            sendMessageToChannel(senderChannel, map);
        }
    }

    /**
     * 处理ICE候选者
     * @param messageVO messageVO对象，封装消息
     * @param target 目标用户id
     */
    private void handleWebRtcCandidate(MessageVO messageVO, Long target) {
        Channel targetChannel = chatChannelGroup.getChannelById(target);
        if (targetChannel != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", messageVO.getType());
            map.put("candidate", messageVO.getCandidate());
            map.put("sdpMid", messageVO.getSdpMid());
            map.put("sdpMLineIndex", messageVO.getSdpMLineIndex());
            map.put("sender", messageVO.getSender());
            map.put("receiver", messageVO.getReceiver());
            map.put("target", messageVO.getTarget());
            sendMessageToChannel(targetChannel, map);
        }
    }

    /**
     * 处理挂断电话
     * @param messageVO messageVO对象，封装消息
     * @param target 目标用户id
     */
    private void handleWebRtcDisconnect(MessageVO messageVO, Long target) {
        Channel targetChannel = chatChannelGroup.getChannelById(target);
        if (targetChannel != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", messageVO.getType());
            map.put("sender", messageVO.getSender());
            map.put("receiver", messageVO.getReceiver());
            map.put("target", messageVO.getTarget());
            sendMessageToChannel(targetChannel, map);
        }
    }

    /**
     * 处理用户发起会话请求
     * @param messageVO messageVO对象，封装消息
     * @param receiver 接收方的id
     * @param sender 发送方的id
     */
    private void handleRequestWebRtc(MessageVO messageVO, Long receiver, Long sender) {
        // 因为是发起请求，因此服务器应将该消息从 sender 向 receiver 转发
        Channel receiverChannel = chatChannelGroup.getChannelById(receiver);
        Channel senderChannel = chatChannelGroup.getChannelById(sender);
        Map<String, Object> map = new HashMap<>(8);
        if (receiverChannel != null) {
            map.put("type", messageVO.getType());
            map.put("security", messageVO.getSecurity());
            map.put("sender", messageVO.getSender());
            map.put("senderName", messageVO.getSenderName());
            map.put("receiver", messageVO.getReceiver());
            sendMessageToChannel(receiverChannel, map);
        } else {
            map.put("accept", -2);
            sendMessageToChannel(senderChannel, map);
        }
    }

    /**
     * 处理响应会话请求
     * @param messageVO messageVO对象，封装消息
     * @param sender 发送方用户id
     */
    private void handleAnswerWebTrc(MessageVO messageVO, Long sender) {
        // 因为是响应对方发起的请求，因此服务器应将该消息从 receiver 向 sender 转发。
        Channel senderChannel = chatChannelGroup.getChannelById(sender);
        if (senderChannel != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", messageVO.getType());
            map.put("security", messageVO.getSecurity());
            map.put("sender", messageVO.getSender());
            map.put("receiver", messageVO.getReceiver());
            map.put("accept", messageVO.getAccept());
            sendMessageToChannel(senderChannel, map);
        }
    }

    /**
     * 默认处理位置消息类型
     * @param channel Channel对象 消息通道
     */
    private void handleDefault(Channel channel) {
        sendMessageToChannel(channel, ResponseData.TYPE_NOT_ALLOWED);
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private ChannelFuture sendMessageToChannel(Channel channel, ResponseData responseData) {
        return channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(responseData))
        );
    }

    private ChannelFuture sendMessageToChannel(Channel channel, Map<String, Object> responseData) {
        return channel.writeAndFlush(
                new TextWebSocketFrame(JSON.toJSONString(responseData))
        );
    }

    private String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            // SSL in use so use Secure WebSockets
            protocol = "wss";
        }
        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

    private void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
        channel.attr(AttributeKey.valueOf(WebSocketHandler.class, "HANDSHAKER")).set(handshaker);
    }

}
