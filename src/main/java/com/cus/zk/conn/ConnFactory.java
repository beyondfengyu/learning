package com.cus.zk.conn;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/10/27
 */
public class ConnFactory {
    private static final Logger logger = LoggerFactory.getLogger(ConnFactory.class);

    private static ZooKeeper zookeeper;

    public static ZooKeeper getZookeeper() throws Exception {
        return getZookeeper("zk.wolfbe.com:2181");
    }

    public static ZooKeeper getZookeeper(String connStr) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        zookeeper = new ZooKeeper(connStr,5000, new Watcher(){

            public void process(WatchedEvent watchedEvent) {
                logger.info("Recevice watch event state:{}",watchedEvent.getState());
                if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
                    if(Event.EventType.None == watchedEvent.getType() && watchedEvent.getPath() == null){
                        countDownLatch.countDown();
                    }else if(Event.EventType.NodeChildrenChanged == watchedEvent.getType()){
                        try {
                            logger.info("children list:{}",zookeeper.getChildren(watchedEvent.getPath(),true));
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
        countDownLatch.await();

        return zookeeper;
    }

}
