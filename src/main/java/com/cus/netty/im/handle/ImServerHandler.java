package com.cus.netty.im.handle;

import com.cus.netty.im.protol.ImProtol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ImServerHandler.class);
    private RequestProcessor processor;

    public ImServerHandler(RequestProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ImProtol) {
            processor.handle(ctx,(ImProtol)msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("channel exception and close",cause);

//        super.exceptionCaught(ctx, cause);
    }
}
