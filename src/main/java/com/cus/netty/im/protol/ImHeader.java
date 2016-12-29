package com.cus.netty.im.protol;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class ImHeader {

    private byte type; //IMTYPE中的常量值

    public ImHeader(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
