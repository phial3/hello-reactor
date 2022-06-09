package com.mine.study.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@ChannelHandler.Sharable
public class MyWsTextInBoundHandle extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelHandler INSTANCE = new MyWsTextInBoundHandle();

    private MyWsTextInBoundHandle() {
        super();
        System.out.println("初始化 WsTextInBoundHandle");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("WsTextInBoundHandle 收到了连接");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        String str = "WsTextInBoundHandle 收到了一条消息, 内容为：" + msg.text();

        System.out.println(str);

        System.out.println("-----------WsTextInBoundHandle 处理业务逻辑-----------");

        String responseStr = "{\"status\":200, \"content\":\"收到\"}";

        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseStr));
        System.out.println("-----------WsTextInBoundHandle 数据回复完毕-----------");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        System.out.println("WsTextInBoundHandle 消息收到完毕");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("WsTextInBoundHandle 连接逻辑中发生了异常");
        cause.printStackTrace();
        ctx.close();
    }
}
