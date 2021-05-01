package com.xziying.appstore.protocol.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * AppSortRpcRequest
 *
 * @author : xziying
 * @create : 2021-04-30 22:44
 */
public class AppStoreRpcRequest {
    String name;        // 方法名
    Class<?>[] type;      // 参数类型
    Object[] args;      // 参数列表

    public AppStoreRpcRequest() {
    }

    public AppStoreRpcRequest(String name, Class<?>[] type, Object[] args) {
        this.name = name;
        this.type = type;
        this.args = args;
    }

    public AppStoreRpcRequest(PackageAppStoreRpcRequest request) throws IOException, ClassNotFoundException {
        // 解析包
        ByteArrayInputStream typeByte = new ByteArrayInputStream(request.getType());
        ByteArrayInputStream argsByte = new ByteArrayInputStream(request.getArgs());
        ObjectInputStream typeOis = new ObjectInputStream(typeByte);
        ObjectInputStream argsOis = new ObjectInputStream(argsByte);
        this.type = (Class<?>[]) typeOis.readObject();
        this.args = (Object[]) argsOis.readObject();
        this.name = new String(request.getName(), StandardCharsets.UTF_8);
    }

    public Class<?>[] getType() {
        return type;
    }

    public void setType(Class<?>[] type) {
        this.type = type;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AppStoreRpcRequest{" +
                "name='" + name + '\'' +
                ", type=" + Arrays.toString(type) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
