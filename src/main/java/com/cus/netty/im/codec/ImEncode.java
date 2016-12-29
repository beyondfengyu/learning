package com.cus.netty.im.codec;

import com.cus.netty.im.protol.ImBody;
import com.cus.netty.im.protol.ImHeader;
import com.cus.netty.im.protol.ImProtol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImEncode extends MessageToByteEncoder<ImProtol> {
    private static final Logger logger = LoggerFactory.getLogger(ImEncode.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ImProtol msg, ByteBuf out) throws Exception {
        ImHeader header = msg.getHeader();
        ImBody body = msg.getBody();
        logger.info("ImEncode type:{}, data:{}", header.getType(), body.getData().toJSONString());
        byte[] data = body.getData().toJSONString().getBytes(Charset.forName("UTF-8"));
        // 1个字节表示类型，4个字节表示载体的长度
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 + data.length);
        buffer.put(header.getType());
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        out.writeBytes(buffer);
    }
}
