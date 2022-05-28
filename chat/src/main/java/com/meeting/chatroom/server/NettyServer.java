package com.meeting.chatroom.server;

import com.meeting.chatroom.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
public class NettyServer {

    @Value("${netty.server.port}")
    private int port;

    private void startServer() {
        //服务端需要2个线程组  boss处理客户端连接  work进行客服端连接之后的处理
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            //服务器 配置
            bootstrap.group(boss,work).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    // 超过30秒没有写，触发写超时
                                    // .addLast("idle-state", new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    // HttpServerCodec：将请求和应答消息解码为HTTP消息
                                    .addLast("http-codec",new HttpServerCodec())
                                    // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
                                    .addLast("aggregator",new HttpObjectAggregator(65536))
                                    // ChunkedWriteHandler：向客户端发送HTML5文件
                                    .addLast("http-chunked",new ChunkedWriteHandler())
                                    // .addLast("websocket", new WebSocketServerProtocolHandler("/ws", "WebSocket", true, 65536 * 10))
                                    // 配置通道处理来进行业务处理
                                    .addLast("handler", new WebSocketHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            //绑定端口  开启事件驱动
            Channel channel = bootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭资源
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    @PostConstruct
    public void init() {
        // 需要开启一个新的线程来启动netty server服务器
        new Thread(this::startServer).start();
    }

}
