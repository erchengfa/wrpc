package com.github.wang.wrpc.context.remoting.netty;


import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.common.RpcConstants;
import com.github.wang.wrpc.context.consumer.RpcClient;
import com.github.wang.wrpc.context.codec.Codec;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author : wang
 * @date : 2019/12/24
 */
@Slf4j
public class NettyClient implements RpcClient {

    private static final int CONNECT_TIMEOUT = 1000 * 5;

    private static final int RECONNECT_COUNT_LIMIT = 10; //连接失败次数限制,如果超过该次就不再进行重连

    private static final int HEARTBEAT_COUNT_LIMIT = 3;

    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(RpcConstants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyClientWorker", true));

    private Bootstrap bootstrap;

    private volatile Channel channel; // volatile, please copy reference to use

    private InetSocketAddress inetSocketAddress;

    private ProviderInfo providerInfo;

    private AtomicInteger heartBeatFailCount = new AtomicInteger(0);

    private AtomicInteger connectionCount = new AtomicInteger(0);

    public NettyClient(ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
        this.inetSocketAddress = new InetSocketAddress(providerInfo.getHost(), providerInfo.getPort());

    }

    public synchronized void connect() {
        if (isActive()) {
            return;
        }
        connectionCount.incrementAndGet(); //连接次数记录
        final NettyClientHandler nettyClientHandler = new NettyClientHandler(this);
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                /**
                 * Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                 * 可以将此功能视为TCP的心跳机制，
                 * 需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
                 */
                .option(ChannelOption.SO_KEEPALIVE, true)
                /**
                 * TCP参数，立即发送数据，默认值为Ture（Netty默认为True而操作系统默认为False）。该值设置Nagle算法的启用，
                 * 该算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量，如果需要发送一些较小的报文，则需要禁用该算法。
                 * Netty默认禁用该算法，从而最小化报文传输延时。
                 */
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout())
                .channel(NioSocketChannel.class);

        ServiceLoader<Codec> serviceLoader = ServiceLoaderFactory.getExtensionLoader(Codec.class);
        Codec codec = serviceLoader.getInstance(providerInfo.getProtocol());

        NettyCodecAdapter nettyCodecAdapter = new NettyCodecAdapter(codec);
        //设置超时时间
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT);

        bootstrap.handler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                        .addLast("decoder", nettyCodecAdapter.getDecoder())
                        .addLast("encoder", nettyCodecAdapter.getEncoder())
                        .addLast("client-idle-handler", new IdleStateHandler(RpcConstants.HEARTBEAT_TIMEOUT, 0, 0, MILLISECONDS))
                        .addLast("handler", nettyClientHandler);

            }
        });

        ChannelFuture future = bootstrap.connect(inetSocketAddress);

        boolean ret = future.awaitUninterruptibly(CONNECT_TIMEOUT, MILLISECONDS);

        if (ret && future.isSuccess()) {
            Channel newChannel = future.channel();
            try {
                // Close old channel
                Channel oldChannel = NettyClient.this.channel; // copy reference
                if (oldChannel != null) {
                    if (log.isInfoEnabled()) {
                        log.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                    }
                    oldChannel.close();
                }
            } finally {
                connectionCount.set(0); //将连接次数清零
                NettyClient.this.channel = newChannel;
            }

        } else if (future.cause() != null) {

        } else {

        }
    }

    public void send(Object message) {
        channel.writeAndFlush(message);
    }

    public ProviderInfo getProviderInfo() {
        return this.providerInfo;
    }

    public void receiveHeartbeat() {
        heartBeatFailCount.set(0);
    }

    public void handleHeartBeatException() {
        int times = heartBeatFailCount.incrementAndGet();
        if (times > HEARTBEAT_COUNT_LIMIT) {//心跳发送失败超过一定的限度
            log.error("心跳发送失败超过一定的限度:{}", providerInfo);
            channel.close(); //关闭连接
            heartBeatFailCount.set(0);//设置心跳次数为0
            this.connect();
        }
    }

    @Override
    public boolean isActive() {
        if (channel == null){
            return false;
        }
        return channel.isActive();
    }

}
