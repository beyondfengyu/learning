package com.cus.netty.im.codec;

import com.alibaba.fastjson.JSONObject;
import com.cus.netty.im.protol.ImHeader;
import com.cus.netty.im.protol.ImProtol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImDecode extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(ImDecode.class);


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readableBytes();
        if (len < 5) {
            return;
        }
        // 消息类型
        byte type = in.markReaderIndex().readByte();
        // 载体数据长度
        int dlen = in.readInt();
        len = in.readableBytes();
        if(len < dlen){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dlen];
        in.readBytes(data, 0, dlen);
        JSONObject json = JSONObject.parseObject(new String(data, Charset.forName("utf-8")));
        out.add(new ImProtol(type,json));
    }
}
