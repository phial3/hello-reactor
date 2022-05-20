package com.mine.study.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SteelyardDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SteelyardDecoder.class);
    private final int BASE_LENGTH = 3 + 4 + 8 + 4;

    private final  int MAX_LENGTH = 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        byte[] b = new byte[in.readableBytes()];
        in.readBytes(b);
        LOGGER.info("收到数据<----传感器：{}", byteToHexString(b));
        out.add(byteToHexString(b));
    }

    public String byteToHexString(byte[] bArray){
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if(sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

}
