package com.cus.netty.im.handle;

import com.alibaba.fastjson.JSONObject;
import com.cus.netty.im.Client;
import com.cus.netty.im.common.IMTYPE;
import com.cus.netty.im.common.JsonKey;
import com.cus.netty.im.common.ProtoBuilder;
import com.cus.netty.im.protol.ImProtol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ImClientHandler.class);
    private Client client;

    public ImClientHandler(Client client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ImProtol) {
            ImProtol protol = (ImProtol) msg;
            switch (protol.getType()) {
                case 0:
                    JSONObject jsonObject = protol.getData();
                    logger.info("[CLIENT]receive message: {}",jsonObject.getString("msg"));
                    break;
                case IMTYPE.PUBLISH:
                    jsonObject = protol.getData();
                    logger.info("[CLIENT]receive message: {}",jsonObject.getString(JsonKey.PUBLISH_MESS));
                    break;
                case 15:
                    break;
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                logger.info("channel idle all");
                client.send(ProtoBuilder.pingProtol());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close().sync();
        logger.error("channel exception and close channel", cause);
        super.exceptionCaught(ctx, cause);
    }
}
