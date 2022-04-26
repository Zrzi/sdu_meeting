package com.meeting.chatroom.handler;

import com.meeting.chatroom.entity.ChatChannelGroup;
import com.meeting.chatroom.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private final ChatChannelGroup chatChannelGroup = SpringUtil.getBean(ChatChannelGroup.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
        } else {
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.ALL_IDLE){
                // 读写空闲，断开连接
//            final Channel channel = ctx.channel();
//            ChannelFuture close = ctx.close();
//            close.addListener(future -> chatChannelGroup.removeChannel(channel));
            }
        }
    }
}
