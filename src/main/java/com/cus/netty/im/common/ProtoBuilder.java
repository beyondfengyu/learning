package com.cus.netty.im.common;

import com.alibaba.fastjson.JSONObject;
import com.cus.netty.im.protol.ImBody;
import com.cus.netty.im.protol.ImProtol;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ProtoBuilder {

    public static ImProtol buildSysMsg(String msg) {
        ImProtol imProtol = new ImProtol(IMTYPE.SYSYTEM);
        ImBody body = new ImBody();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", msg);
        body.setData(jsonObject);
        imProtol.setBody(body);
        return imProtol;
    }

    public static ImProtol newClientJoin(String clientId) {
        ImProtol imProtol = new ImProtol(IMTYPE.CONNECTION);
        ImBody body = new ImBody();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonKey.CLIENT_KEY, Constants.CLIENT_KEY);
        jsonObject.put(JsonKey.CLIENT_ID, clientId);
        body.setData(jsonObject);
        imProtol.setBody(body);
        return imProtol;
    }

    public static ImProtol publishMess(String mess) {
        ImProtol imProtol = new ImProtol(IMTYPE.PUBLISH);
        ImBody body = new ImBody();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonKey.PUBLISH_MESS, mess);
        body.setData(jsonObject);
        imProtol.setBody(body);
        return imProtol;
    }

    public static ImProtol pingProtol() {
        ImProtol imProtol = new ImProtol(IMTYPE.PING,new JSONObject());
        return imProtol;
    }
}
