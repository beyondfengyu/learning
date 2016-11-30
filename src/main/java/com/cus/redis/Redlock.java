package com.cus.redis;

import redis.clients.jedis.Jedis;

/**
 * 分布式锁
 *
 * @author ason
 * @version 1.0
 * @date 2016/11/30
 */
public class Redlock {
    private static final String LOCK_KEY = "redis_lock";
    private static final int RETRY_TIME = 10 * 1000; //等待锁的时间
    private static final int EXPIRE_TIME = 60 * 1000;//锁超时的时间
    private boolean locked;
    private long lockValue;

    public synchronized boolean lock(Jedis jedis){
        int retryTime = RETRY_TIME;
        while(retryTime>0){
            lockValue = System.currentTimeMillis();
//            if(jedis.setnx(LOCK_KEY,))
        }
        return false;
    }

    public synchronized void unlock(){

    }
}
