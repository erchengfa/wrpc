package com.github.wang.wrpc.context.remoting.netty;


import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.common.Server;
import com.github.wang.wrpc.context.common.RpcConstants;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.codec.Codec;
import com.github.wang.wrpc.context.remoting.disruptor.EventDisruptor;
import com.github.wang.wrpc.context.remoting.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author : wang
 * @date : 2019/12/24
 */
public class NettyServer implements Server {

    private ServerBootstrap bootstrap;

    private io.netty.channel.Channel channel;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ServerConfig serverConfig;

    private InetSocketAddress inetSocketAddress;

    private EventDisruptor eventDisruptor;

    public NettyServer(ServerConfig serverConfig){
        this.serverConfig = serverConfig;
        this.inetSocketAddress = new InetSocketAddress(serverConfig.getHost(),serverConfig.getPort());
        eventDisruptor = new EventDisruptor(serverConfig.getMessageHandleThreadSize(),serverConfig.getRingBufferSize());
    }

    public void doOpen(){
        bootstrap = new ServerBootstrap();

        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", true));
        workerGroup = new NioEventLoopGroup(RpcConstants.DEFAULT_IO_THREADS,
                new DefaultThreadFactory("NettyServerWorker", true));

        ServiceLoader<Codec> codecLoader = ServiceLoaderFactory.getExtensionLoader(Codec.class);
        Codec codec = codecLoader.getInstance(serverConfig.getProtocol(),
                new Class[]{String.class},new String[]{serverConfig.getSerialization()});

        NettyCodecAdapter nettyCodecAdapter = new NettyCodecAdapter(codec);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder", nettyCodecAdapter.getDecoder())
                                .addLast("encoder", nettyCodecAdapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, RpcConstants.HEARTBEAT_TIMEOUT * 3, MILLISECONDS))
                                .addLast("handler", new NettyServerHandler(eventDisruptor));
                    }
                });

        eventDisruptor.start();

        // bind
        ChannelFuture channelFuture = bootstrap.bind(inetSocketAddress);
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
    }

    @Override
    public void open() {
        this.doOpen();
    }

    @Override
    public void registerServiceHanler(MessageHandler serviceHandler) {
        eventDisruptor.registerServiceHanler(serviceHandler);
    }

}
