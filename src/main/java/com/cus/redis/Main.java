package com.cus.redis;

import redis.clients.jedis.Jedis;

/**
 * @author ason
 * @version 1.0
 * @date 2016/11/29
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("192.168.75.129",6379);
        long result = jedis.setnx("zk_lock",System.currentTimeMillis()+"");
        jedis.expire("zk_lock",5);
        if(result==1){
            Thread.sleep(5000);
            System.out.println("reuslt:"+System.currentTimeMillis());
        }
        jedis.del("zk_lock");
    }
}
