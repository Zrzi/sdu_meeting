package com.meeting.chatroom.handler;

import com.meeting.chatroom.entity.ChatChannelGroup;
import com.meeting.common.util.JwtTokenUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

@ChannelHandler.Sharable
public class AuthorizationHandler extends ChannelInboundHandlerAdapter {

    private final JwtTokenUtil jwtTokenUtil;

    public AuthorizationHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String token = request.headers().get("Authorization");
            Long uid = null;
            if (token == null || !jwtTokenUtil.validateToken(token)
                    || (uid = jwtTokenUtil.getUserIdFromToken(token)) == null) {
                refuse(ctx);
                return;
            }
            ChatChannelGroup.LOCK.writeLock().lock();
            try {
                ChatChannelGroup.ID_CHANNEL_REF.put(uid, ctx.channel());
                ChatChannelGroup.CHANNEL_ID_REF.put(ctx.channel(), uid);
            } finally {
                ChatChannelGroup.LOCK.writeLock().unlock();
            }
        }
        ctx.fireChannelRead(msg);
    }

    private void refuse(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush(
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED)
        );
        ctx.channel().close();
    }

}
