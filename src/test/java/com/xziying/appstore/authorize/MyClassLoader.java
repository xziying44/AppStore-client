package com.xziying.appstore.authorize;

import com.xziying.appstore.cloud.gateway.VerificationCodeCloud;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MyClassLoader
 *
 * @author : xziying
 * @create : 2021-04-11 12:25
 */
public class MyClassLoader  extends ClassLoader{

    public Class<?> load(String clazz, String fileName){
        byte[] bytes=null;
        try {
            InputStream is=new FileInputStream(fileName);
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            byte[] buf=new byte[1024];
            int r=0;
            while ((r=is.read(buf))!=-1){
                bos.write(buf,0,r);
            }
            bytes=bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defineClass(clazz,bytes,0,bytes.length);
    }


    @Test
    public void test1() throws IllegalAccessException, InstantiationException {
        MyClassLoader myClassLoader = new MyClassLoader();
        Class<?> load = myClassLoader.load("com.xziying.appstorecloud.control.authorize.VerificationCodeCloudImpl", "C:\\Users\\xziying\\Desktop\\VerificationCodeCloudImpl.classa");
        VerificationCodeCloud o = (VerificationCodeCloud) load.newInstance();

    }
}