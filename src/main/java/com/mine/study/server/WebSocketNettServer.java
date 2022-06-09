package com.mine.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WebSocketNettServer {
    public static void main(String[] args) {

        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .localAddress(8089)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // HTTP 请求解码和响应编码
                                    .addLast(new HttpServerCodec())
                                    // HTTP 压缩支持
                                    .addLast(new HttpContentCompressor())
                                    // HTTP 对象聚合完整对象
                                    .addLast(new HttpObjectAggregator(65536))
                                    // WebSocket支持
                                    .addLast(new WebSocketServerProtocolHandler("/netty-ws"))
                                    .addLast(MyWsTextInBoundHandle.INSTANCE);
                        }
                    });

            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind().sync();
            System.out.println("WebSocketNettServer启动成功");

            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully().syncUninterruptibly();
            work.shutdownGracefully().syncUninterruptibly();
        }

    }
}
