package com.xziying.appstore.protocol;

import com.xziying.appstore.protocol.domain.AppStoreRpcResponse;
import com.xziying.appstore.protocol.domain.PackageAppStoreRpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * AppStoreRpcClientDecoder 客户端解码器
 *
 * @author : xziying
 * @create : 2021-05-01 10:25
 */
public class AppStoreRpcResponseDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int headLen = in.readInt();
        byte[] headByte = new byte[headLen];
        in.readBytes(headByte);
        String head = new String(headByte, StandardCharsets.UTF_8);
        if (head.equals("APP_STORE_RPC_RESPONSE")){
            // RPC 协议
            int objLen = in.readInt();
            byte[] objByte = new byte[objLen];
            in.readBytes(objByte);

            PackageAppStoreRpcResponse response = new PackageAppStoreRpcResponse();
            response.setObjLen(objLen);
            response.setObj(objByte);
            AppStoreRpcResponse reply = new AppStoreRpcResponse(response);
            out.add(reply);
        }
    }
}
