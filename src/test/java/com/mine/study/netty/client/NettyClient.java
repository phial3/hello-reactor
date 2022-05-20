package com.mine.study.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(group)
                //该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());

        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 9000).sync();
            logger.info("客户端连接成功....");
            Long id = 2231233312223333L;
            while (true) {
                int num = 0;
                //发送消息
                for (int i = 0; i < 500; i++) {
                    if (i > 400) {
                        future.channel().writeAndFlush(i + "|" + id + "|" + num + "\r");
                        Thread.sleep(new Random().nextInt(10) * 100);
                        continue;
                    }
                    num = new Random().nextInt(40);
                    future.channel().writeAndFlush(i + "|" + id + "|" + num + "\r");
                    Thread.sleep(new Random().nextInt(5) * 100);
                }
                Thread.sleep(5 * 60 * 1000);
            }

            // 等待连接被关闭
            //future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyClient().start();
    }

}
