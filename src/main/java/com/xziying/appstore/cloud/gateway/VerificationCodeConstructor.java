package com.xziying.appstore.cloud.gateway;

/**
 * VerificationCodeConstructor 构造器
 *
 * @author : xziying
 * @create : 2021-04-11 12:51
 */
public class VerificationCodeConstructor extends ClassLoader{

    public Class<?> loadClass(String name, byte[] bytes, int off, int len){
        return defineClass(name, bytes, off, len);
    }
}
