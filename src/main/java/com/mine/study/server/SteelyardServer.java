package com.mine.study.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SteelyardServer implements CommandLineRunner, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(SteelyardServer.class);
    ByteBuf delimiter = Unpooled.copiedBuffer("\r".getBytes());

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture cf;
    private TaskScheduler taskScheduler;

    @Value("${server.port}")
    private int serverPort;

    public SteelyardServer(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void start() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(bossGroup, workerGroup)                // 绑定线程池
                    .channel(NioServerSocketChannel.class)  // 指定使用的channel
                    .localAddress(serverPort)               // 绑定监听端口
                    .option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            logger.info("收到新的客户端连接: {}", ch.remoteAddress());
                            //心跳检测,参数说明：[长时间未写:长时间未读:长时间未读写:时间单位]~[读写是对连接本生而言，写：未向服务端发送消息，读：未收到服务端的消息]
                            ch.pipeline().addLast(new IdleStateHandler(0, 0, 5, TimeUnit.MINUTES));
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            ch.pipeline().addLast(new SteelyardDecoder());
                            ch.pipeline().addLast(new SteelyardEncoder());
                            ch.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new SteelyardServerHandler(taskScheduler));
                        }
                    });
            cf = sb.bind().sync();  // 服务器异步创建绑定
            logger.info("SteelyardServer listening:" + cf.channel().localAddress());
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void destroy() {
        logger.info("SteelyardServer stop:" + cf.channel().localAddress());
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
