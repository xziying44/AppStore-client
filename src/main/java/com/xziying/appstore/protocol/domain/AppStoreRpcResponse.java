package com.xziying.appstore.protocol.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * AppStoreRpcResponse
 *
 * @author : xziying
 * @create : 2021-05-01 10:14
 */
public class AppStoreRpcResponse {
    Object reply;   // 返回结果

    public AppStoreRpcResponse() {
    }

    public AppStoreRpcResponse(PackageAppStoreRpcResponse response) throws IOException, ClassNotFoundException {
        // 解析包
        ByteArrayInputStream replyByte = new ByteArrayInputStream(response.getObj());
        ObjectInputStream replyOis = new ObjectInputStream(replyByte);
        this.reply = replyOis.readObject();
    }

    public AppStoreRpcResponse(Object reply) {
        this.reply = reply;
    }

    public Object getReply() {
        return reply;
    }

    public void setReply(Object reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        return "AppStoreRpcResponse{" +
                "reply=" + reply +
                '}';
    }
}
