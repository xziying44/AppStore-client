package com.xziying.appstore.protocol.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * PackageAppStoreRpcResponse
 *
 * @author : xziying
 * @create : 2021-05-01 10:15
 */
public class PackageAppStoreRpcResponse {
    byte[] head = "APP_STORE_RPC_RESPONSE".getBytes(StandardCharsets.UTF_8);  // 协议头
    int headLen = head.length;                                               // 协议头长度
    int objLen; // 对象长度
    byte[] obj; // 对象

    public PackageAppStoreRpcResponse() {
    }

    public PackageAppStoreRpcResponse(AppStoreRpcResponse response) throws IOException {
        ByteArrayOutputStream objByte = new ByteArrayOutputStream();
        ObjectOutputStream objOos = new ObjectOutputStream(objByte);
        objOos.writeObject(response.getReply());
        objOos.flush();
        this.obj = objByte.toByteArray();
        this.objLen = this.obj.length;
    }


    public byte[] getHead() {
        return head;
    }

    public void setHead(byte[] head) {
        this.head = head;
    }

    public int getHeadLen() {
        return headLen;
    }

    public void setHeadLen(int headLen) {
        this.headLen = headLen;
    }

    public int getObjLen() {
        return objLen;
    }

    public void setObjLen(int objLen) {
        this.objLen = objLen;
    }

    public byte[] getObj() {
        return obj;
    }

    public void setObj(byte[] obj) {
        this.obj = obj;
    }
}
