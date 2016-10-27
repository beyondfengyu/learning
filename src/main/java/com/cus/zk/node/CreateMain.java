package com.cus.zk.node;

import com.cus.zk.conn.ConnFactory;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/10/27
 */
public class CreateMain {
    private static final Logger logger = LoggerFactory.getLogger(CreateMain.class);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = ConnFactory.getZookeeper();
//        syncCreateNode(zooKeeper);
        asyncCreateNode(zooKeeper);
    }

    public static String syncCreateNode(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        String path = zooKeeper.create("/zk-node", "ephemeral-test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        logger.info("syncCreateNode ephemeral path:{}", path);
        path = zooKeeper.create("/zk-node", "ephemeral_sequential-test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.info("syncCreateNode ephemeral_sequential path:{}", path);
        return path;
    }

    public static void asyncCreateNode(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        zooKeeper.create("/zk-node", "async-ephemeral-test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL, new AsyncCallback.StringCallback() {
                    public void processResult(int ic, String path, Object ctx, String name) {
                        logger.info("asyncCreateNode ephemeral ic:{}, path:{}, ctx:{}, name:{}",ic, path, ctx, name);
                    }
                }, "async-ephemeral context");

        zooKeeper.create("/zk-node", "async-ephemeral_sequential-test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL, new AsyncCallback.StringCallback() {
                    public void processResult(int ic, String path, Object ctx, String name) {
                        logger.info("asyncCreateNode ephemeral_sequential ic:{}, path:{}, ctx:{}, name:{}",ic, path, ctx, name);
                    }
                }, "async-ephemeral_sequential context");
        TimeUnit.SECONDS.sleep(5);
    }
}
