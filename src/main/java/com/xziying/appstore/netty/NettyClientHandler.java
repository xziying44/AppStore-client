package com.xziying.appstore.netty;


import com.xziying.appstore.protocol.domain.AppStoreRpcRequest;
import com.xziying.appstore.protocol.domain.AppStoreRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * NettyClientHandler
 *
 * @author : xziying
 * @create : 2021-04-30 21:31
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<AppStoreRpcResponse> {
    AppStoreRpcRequest request;
    AppStoreRpcResponse response;


    public NettyClientHandler(AppStoreRpcRequest request) {
        this.request = request;
    }

    // 同道就绪触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AppStoreRpcResponse msg) throws Exception {
        response = msg;
        ctx.close();
    }

    public AppStoreRpcResponse getResponse() {
        return response;
    }
}
