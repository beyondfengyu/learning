package com.cus.netty.im.handle;

import com.alibaba.fastjson.JSONObject;
import com.cus.netty.im.Server;
import com.cus.netty.im.common.Constants;
import com.cus.netty.im.common.IMTYPE;
import com.cus.netty.im.common.JsonKey;
import com.cus.netty.im.common.ProtoBuilder;
import com.cus.netty.im.protol.ImProtol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

    public void handle(ChannelHandlerContext ctx, ImProtol imProtol) {
        switch (imProtol.getType()) {
            case IMTYPE.CONNECTION: //新连接加入
                JSONObject data = imProtol.getData();
                String key = data.getString(JsonKey.CLIENT_KEY);
                if(!StringUtil.isNullOrEmpty(key) && Constants.CLIENT_KEY.equals(key)) {
                    String clientId = data.getString(JsonKey.CLIENT_ID);
                    if (Server.clients.containsKey(clientId)) {
                        Channel ch = Server.clients.get(clientId);
                        if (ch.isActive()) {
                            ctx.channel().close();
                            logger.warn("client {} has exist", clientId);
                            break;
                        }
                    }
                    Server.clients.put(clientId, ctx.channel());
                    Server.sendBroadcast(ProtoBuilder.buildSysMsg(String.format("client %s join", clientId)));
                }else{
                    ctx.channel().close();
                    logger.warn("client key is null or error");
                }
                break;
            case IMTYPE.PUBLISH:
                String mess = imProtol.getData().getString(JsonKey.PUBLISH_MESS);
                Server.sendBroadcast(ProtoBuilder.publishMess(mess));
                break;
            case IMTYPE.PING:
                logger.info("receive client ping message");
                break;
        }
    }
}
