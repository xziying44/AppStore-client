package com.xziying.appstore.proxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

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
                        //连接服务器
                        Socket client = new Socket(ip, port);
                        //获取对象输出流
                        ObjectOutputStream oos=new ObjectOutputStream(client.getOutputStream());
                        //发送方法名称给服务器
                        oos.writeObject(method.getName());
                        oos.flush();
                        //发送方法的参数类型
                        oos.writeObject(method.getParameterTypes());
                        oos.flush();
                        //发送具体的参数给服务器
                        oos.writeObject(args);
                        oos.flush();
                        //创建对象输入流 用于接受服务器返回的结果
                        ObjectInputStream ois=new ObjectInputStream(client.getInputStream());
                        Object o = ois.readObject();
                        oos.close();
                        return o;
                    }
                });
    }
}