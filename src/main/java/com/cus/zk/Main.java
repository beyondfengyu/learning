package com.cus.zk;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

/**
 * @author laochunyu
 * @date 2016/10/26
 */
public class Main {

    static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws NoSuchAlgorithmException {
        logger.info(DigestAuthenticationProvider.generateDigest("foo333:zk-book333"));
    }
}
