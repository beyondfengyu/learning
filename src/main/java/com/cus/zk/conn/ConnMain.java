package com.cus.zk.conn;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author laochunyu
 * @date 2016/10/27
 */
public class ConnMain implements Watcher {
    private static final Logger logger = LoggerFactory.getLogger(ConnMain.class);

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper("zk.wolfbe.com:2181",5000, new ConnMain());
        logger.info("zookeeper state:{}", zooKeeper.getState());

        countDownLatch.await();

        long sessionID = zooKeeper.getSessionId();
        byte[] psswd = zooKeeper.getSessionPasswd();
        logger.info("sessionID:{}, passwd:{}",sessionID,new String(psswd,"utf-8"));

        zooKeeper = new ZooKeeper("zk.wolfbe.com:2182",5000,new ConnMain(),sessionID,psswd);

        TimeUnit.SECONDS.sleep(30);
    }

    public void process(WatchedEvent watchedEvent) {
        logger.info("Recevice watch event state:{}"+watchedEvent.getState());
        if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
            countDownLatch.countDown();
        }
    }
}
