package com.cus.zk.acl;

import com.cus.zk.conn.ConnFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 测试权限信息内容
 *
 * @author laochunyu
 * @version 1.0
 * @date 2016/10/27
 */
public class DelMain {
    private static final Logger logger = LoggerFactory.getLogger(DelMain.class);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = ConnFactory.getZookeeper();
        zooKeeper.addAuthInfo("digest","myname:true".getBytes());
        //调用前需要保证/zk-node结点已经存在
        String path = "/zk-node-acl";
        String path2 = "/zk-node-acl/child1";
//        syncCreateNode(zooKeeper, "/zk-node-acl");
//        syncCreateNode(zooKeeper, "/zk-node-acl/child1");

        zooKeeper = ConnFactory.getZookeeper();
        zooKeeper.addAuthInfo("digest","myname:false".getBytes());
        try{
            zooKeeper.delete(path2,-1);
            logger.info("myname:false del success");
        }catch (Exception e){
            logger.error("myname:false del node error,",e);
        }

        zooKeeper = ConnFactory.getZookeeper();
        zooKeeper.addAuthInfo("digest","myname:true".getBytes());
        try{
            zooKeeper.delete(path2,-1);
            zooKeeper.delete(path,-1);
            logger.info("myname:true del success");
        }catch (Exception e){
            logger.error("myname:true del node error,",e);
        }


        TimeUnit.SECONDS.sleep(30);
    }

    public static String syncCreateNode(ZooKeeper zooKeeper,String path) throws KeeperException, InterruptedException {
        zooKeeper.create(path, ("persistent-test"+path).getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        return path;
    }
}
