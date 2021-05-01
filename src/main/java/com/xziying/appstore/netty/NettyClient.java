package com.xziying.appstore.netty;

import com.xziying.appstore.protocol.AppStoreRpcRequestEncoder;
import com.xziying.appstore.protocol.AppStoreRpcResponseDecoder;
import com.xziying.appstore.protocol.domain.AppStoreRpcRequest;
import com.xziying.appstore.protocol.domain.AppStoreRpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * NettyClient
 *
 * @author : xziying
 * @create : 2021-04-30 21:24
 */
public class NettyClient {
    // 创建一个线程循环
    static EventLoopGroup groups = new NioEventLoopGroup();
    String ip;
    int port;

    public NettyClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Object request(AppStoreRpcRequest request) throws InterruptedException {
        NettyClientHandler nettyClientHandler = new NettyClientHandler(request);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(groups) // 设置线程组
                .channel(NioSocketChannel.class)    //设置通道实现类
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new AppStoreRpcRequestEncoder()); // 请求协议编码器
                        ch.pipeline().addLast(new AppStoreRpcResponseDecoder()); // 客户端解码器
                        ch.pipeline().addLast(nettyClientHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        channelFuture.channel().closeFuture().sync();
        AppStoreRpcResponse response = nettyClientHandler.getResponse();
        return response.getReply();
    }

    public static void close(){
        groups.shutdownGracefully();
    }

}
