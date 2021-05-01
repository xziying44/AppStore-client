package com.xziying.appstore.protocol.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * AppStoreRpcRequestByte
 *
 * @author : xziying
 * @create : 2021-04-30 22:50
 */
public class PackageAppStoreRpcRequest {
    byte[] head = "APP_STORE_RPC_REQUEST".getBytes(StandardCharsets.UTF_8);  // 协议头
    int headLen = head.length;                                               // 协议头长度
    int nameLen;                            // name对象长度
    int typeLen;                          // method对象长度
    int argsLen;                            // args对象长度
    byte[]  name;
    byte[]  type;
    byte[]  args;

    public PackageAppStoreRpcRequest() {
    }



    public PackageAppStoreRpcRequest(AppStoreRpcRequest request) throws IOException {
        // 构建包
        ByteArrayOutputStream methodByte = new ByteArrayOutputStream();
        ByteArrayOutputStream argsByte = new ByteArrayOutputStream();
        ObjectOutputStream methodOos = new ObjectOutputStream(methodByte);
        ObjectOutputStream argsOos = new ObjectOutputStream(argsByte);
        methodOos.writeObject(request.getType());
        argsOos.writeObject(request.getArgs());
        methodOos.flush();
        argsOos.flush();
        this.type = methodByte.toByteArray();
        this.args = argsByte.toByteArray();
        this.name = request.getName().getBytes(StandardCharsets.UTF_8);
        this.typeLen = this.type.length;
        this.argsLen = this.args.length;
        this.nameLen = this.name.length;
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

    public int getTypeLen() {
        return typeLen;
    }

    public void setTypeLen(int typeLen) {
        this.typeLen = typeLen;
    }

    public byte[] getType() {
        return type;
    }

    public void setType(byte[] type) {
        this.type = type;
    }

    public int getArgsLen() {
        return argsLen;
    }

    public void setArgsLen(int argsLen) {
        this.argsLen = argsLen;
    }


    public byte[] getArgs() {
        return args;
    }

    public void setArgs(byte[] args) {
        this.args = args;
    }

    public int getNameLen() {
        return nameLen;
    }

    public void setNameLen(int nameLen) {
        this.nameLen = nameLen;
    }

    public byte[] getName() {
        return name;
    }

    public void setName(byte[] name) {
        this.name = name;
    }


}
