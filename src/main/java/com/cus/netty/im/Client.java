package com.cus.netty.im;

import com.cus.netty.im.codec.ImDecode;
import com.cus.netty.im.codec.ImEncode;
import com.cus.netty.im.common.Constants;
import com.cus.netty.im.common.ProtoBuilder;
import com.cus.netty.im.handle.ImClientHandler;
import com.cus.netty.im.protol.ImProtol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Scanner;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private NioEventLoopGroup workGrp;
    private Bootstrap b;
    private Channel ch;


    public Client init() {
        workGrp = new NioEventLoopGroup(2);
        b = new Bootstrap();
        b.group(workGrp)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new ImEncode(),
                                new ImDecode(),
                                new IdleStateHandler(0, 0, 60), // 心跳
                                new ImClientHandler(Client.this)
                        );
                    }
                });
        return this;
    }

    public void start() {
        try {
            ChannelFuture cf = b.connect(Constants.SERVER_HOST, Constants.SERVER_PORT).sync();
            ch = cf.channel();
            logger.info("Client start success!!");
        } catch (InterruptedException e) {
            logger.error("Client start fail", e);
        }
    }

    public void close() {
        try {
            workGrp.shutdownGracefully();
            ch.close().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void send(ImProtol imProtol) {
        if (!ch.isActive()) {
            logger.info("channel is not active!!!");
            return;
        }
        ch.writeAndFlush(imProtol);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init()
                .start();
        client.send(ProtoBuilder.newClientJoin("CLIENT_"+3));

        while (true) {
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            if (str.equals("q")) {
                break;
            }
            client.send(ProtoBuilder.publishMess(new Date(0).toString()));
        }
    }
}
