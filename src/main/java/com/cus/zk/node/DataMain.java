package com.cus.zk.node;

import com.cus.zk.conn.ConnFactory;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/10/27
 */
public class DataMain {
    private static final Logger logger = LoggerFactory.getLogger(DataMain.class);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = ConnFactory.getZookeeper();
        //调用前需要保证/zk-node结点已经存在
//        String path = syncCreateNode(zooKeeper, "/zk-node/child11");
//        //注意，这里传入的是父结点的路径
//        asyncDataChild(zooKeeper,"/zk-node");
////        syncDataChild(zooKeeper,"/zk-node");
//        path = syncCreateNode(zooKeeper, "/zk-node/child22");
        for(int i=1; i<100;i++){
            getData(zooKeeper,"/zk-node");
            TimeUnit.SECONDS.sleep(3);
        }
    }

    public static String syncCreateNode(ZooKeeper zooKeeper,String path) throws KeeperException, InterruptedException {
//        zooKeeper.create("/zk-node", ("ephemeral-test"+path).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        String path2 = zooKeeper.create(path, ("ephemeral-test"+path).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        logger.info("syncCreateNode ephemeral path:{}", path2);
        return path2;
    }

    public static void syncDataChild(ZooKeeper zooKeeper,String path) throws KeeperException, InterruptedException {
        List<String> list = zooKeeper.getChildren(path,true);
        logger.info("syncDataChild list:{}", list);
    }

    public static void asyncDataChild(ZooKeeper zooKeeper,String path) throws KeeperException, InterruptedException {
        zooKeeper.getChildren(path, true, new AsyncCallback.Children2Callback() {
            public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
                logger.info("asyncDataChild list:{}, stat:{}, ctx:{}", children, stat, ctx);

            }
        },"asyncDataChild ctx data");
    }

    public static void getData(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData(path,false,null);
        logger.info("getData data: {}",new String(data));
    }
}
