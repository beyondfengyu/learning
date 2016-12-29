package com.cus.netty.im;

import com.cus.netty.im.codec.ImDecode;
import com.cus.netty.im.codec.ImEncode;
import com.cus.netty.im.handle.ImServerHandler;
import com.cus.netty.im.handle.RequestProcessor;
import com.cus.netty.im.protol.ImProtol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author laochunyu
 * @version 1.0
 * @date 2016/12/29
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public static ConcurrentMap<String, Channel> clients = new ConcurrentHashMap<>();

    private NioEventLoopGroup bossGrp;
    private NioEventLoopGroup workGrp;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private ServerBootstrap b;

    public Server init() {
        bossGrp = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory() {
            private AtomicInteger thIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyBoss_"+thIndex.incrementAndGet());
            }
        });
        workGrp = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 4, new ThreadFactory() {
            private AtomicInteger thIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"NettyWork_"+thIndex.incrementAndGet());
            }
        });
        defaultEventExecutorGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 4, //
                new ThreadFactory() {
                    private AtomicInteger thIndex = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "NettyServerCodecThread_" + thIndex.incrementAndGet());
                    }
                });
        b = new ServerBootstrap();
        b.group(bossGrp, workGrp)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress(15688))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ImDecode())
                                .addLast(new ImEncode())
                                .addLast(new IdleStateHandler(0, 0, 10))
                                .addLast(new ImServerHandler(new RequestProcessor())
                        );
                    }
                });
        return this;
    }

    public void start(){
        try {
            ChannelFuture future = b.bind().sync();
            logger.info("Server start finish!!!");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("server error", e);
            System.exit(-1);
        }finally {
            close();
        }
    }

    public void close() {
        bossGrp.shutdownGracefully();
        workGrp.shutdownGracefully();
        defaultEventExecutorGroup.shutdownGracefully();
    }

    /**
     * 发送广播
     * @param imProtol
     */
    public static void sendBroadcast(ImProtol imProtol){
        Set<String> keys = clients.keySet();
        for(String key: keys) {
            clients.get(key).writeAndFlush(imProtol);
        }
    }

    public static void send(Channel ch, ImProtol imProtol) {
        ch.writeAndFlush(imProtol);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.init()
                .start();
    }
}
