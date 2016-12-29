package com.cus.netty.im.protol;

import com.alibaba.fastjson.JSONObject;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImProtol{

    private ImHeader header;
    private ImBody body;

    public ImProtol(byte type) {
        header = new ImHeader(type);
        body = new ImBody();
    }

    public ImProtol(byte type, JSONObject data) {
        header = new ImHeader(type);
        body = new ImBody(data);
    }

    public ImHeader getHeader() {
        return header;
    }

    public void setHeader(ImHeader header) {
        this.header = header;
    }

    public ImBody getBody() {
        return body;
    }

    public void setBody(ImBody body) {
        this.body = body;
    }

    public byte getType() {
        return getHeader().getType();
    }

    public JSONObject getData(){
        return getBody().getData();
    }
}
