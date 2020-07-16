package org.ang.service;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.ang.handler.AngIdleStateHandler;
import org.ang.handler.BusinessWebSocketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;


/**
 * @author Liang
 */
public class NettyServer implements Runnable {
    @Value("${readerIdleTime}")
    int readerIdleTime;
    private final int port;

    private static final Logger log = LogManager.getLogger(NettyServer.class);
    private final ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        //创建两个线程组 含有的子线程NioEventLoop的个数默认为CUP核数的二倍  bossGroup 只负责处理请求  workGroup会负责和客户端的业务处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //创建服务端启动对象
            ServerBootstrap sb = new ServerBootstrap();
            //初始化服务器队列连接大小
            sb.option(ChannelOption.SO_BACKLOG, 5214);
            //// 绑定线程池
            sb.group(workGroup, bossGroup)
                    // 指定使用的channel
                    .channel(NioServerSocketChannel.class)
                    // 绑定监听端口
                    .localAddress(this.port)
                    // 绑定客户端连接时候触发操作
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                            ch.pipeline().addLast(new HttpServerCodec());
                            //以块的方式来写的处理器
                            ch.pipeline().addLast(new ChunkedWriteHandler());
//                            pipeline.addLast("http-encodec",new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpObjectAggregator(100000));
                            ch.pipeline().addLast(new AngIdleStateHandler(readerIdleTime, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new BusinessWebSocketHandler());
//                            ch.pipeline().addLast(new BusinessWebSocketHandler1());
                        }
                    });
            // 服务器异步创建绑定
            ChannelFuture cf = sb.bind().sync();
            log.info(NettyServer.class + "|･ω･｀)|･ω･｀)|･ω･｀)|･ω･｀) 启动正在监听： " + cf.channel().localAddress());
            // 关闭服务器通道
            cf.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            // 释放线程池资源
            workGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }

    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            log.error("启动失败");
        }

    }
}
