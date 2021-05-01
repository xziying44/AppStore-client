package com.xziying.appstore.proxy;

import com.xziying.appstore.netty.NettyClient;
import com.xziying.appstore.protocol.domain.AppStoreRpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ProxyPattern
 *
 * @author : xziying
 * @create : 2021-03-29 20:13
 */
public class ProxyPattern {
    public static Object getProxy(Class<?> c,String ip,int port){
        return Proxy.newProxyInstance(c.getClassLoader(),
                new Class[]{c},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        AppStoreRpcRequest request = new AppStoreRpcRequest(method.getName(), method.getParameterTypes(), args);
                        NettyClient client = new NettyClient(ip, port);
                        return client.request(request);
                    }
                });
    }
}