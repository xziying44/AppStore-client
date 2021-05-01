package com.xziying.appstore.protocol;


import com.xziying.appstore.protocol.domain.AppStoreRpcRequest;
import com.xziying.appstore.protocol.domain.PackageAppStoreRpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * AppStoreRpcRequestEncoder
 *  编码器
 * @author : xziying
 * @create : 2021-04-30 22:47
 */
public class AppStoreRpcRequestEncoder extends MessageToByteEncoder<AppStoreRpcRequest> {


    @Override
    protected void encode(ChannelHandlerContext ctx, AppStoreRpcRequest msg, ByteBuf out) throws Exception {
        PackageAppStoreRpcRequest request = new PackageAppStoreRpcRequest(msg);
        // 发送协议头
        out.writeInt(request.getHeadLen());
        out.writeBytes(request.getHead());
        // 发送协议内容
        out.writeInt(request.getNameLen());
        out.writeInt(request.getTypeLen());
        out.writeInt(request.getArgsLen());
        out.writeBytes(request.getName());
        out.writeBytes(request.getType());
        out.writeBytes(request.getArgs());
    }
}
