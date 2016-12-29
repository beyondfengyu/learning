package com.cus.netty.im.protol;

import com.alibaba.fastjson.JSONObject;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImBody{

    private JSONObject data;

    public ImBody(){

    }

    public ImBody(JSONObject jsonObject) {
        this.data = jsonObject;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public String toString(){
        return "ImBody data is:"+data.toJSONString();
    }
}
