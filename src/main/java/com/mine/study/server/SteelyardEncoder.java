package com.mine.study.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SteelyardEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        try {
            String data = (String) msg;
            System.out.println("------");

            out.writeCharSequence(data, CharsetUtil.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
