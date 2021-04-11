package com.xziying.appstore.test;

import com.xziying.appstore.plugIn.ProtocolEntry;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * MyTest
 *
 * @author : xziying
 * @create : 2021-01-25 17:35
 */
public class MyTest {
    @Test
    public void test1() throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String path = "G:\\JavaProgram\\新锐项目\\打包" +
                "项目\\app\\com.xziying.collectionAndForwarding.jar";//jar文件需放在工程目录下

        //loadJar(path);
        Class<?> aClass = Class.forName("com.xziying.collectionAndForwarding.Collection");
        ProtocolEntry protocolEntry = (ProtocolEntry) aClass.newInstance();

    }
    private static void loadJar(String jarPath) throws Exception {
        File jarFile = new File(jarPath); // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹

        if (!jarFile.exists()) {
            System.out.println("jar file not found.");
            return;
        }

        //获取类加载器的addURL方法，准备动态调用
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
            return;
        }

        // 获取方法的访问权限，保存原始值
        boolean accessible = method.isAccessible();
        try {
            //修改访问权限为可写
            if (!accessible) {
                method.setAccessible(true);
            }

            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            //获取jar文件的url路径
            java.net.URL url = jarFile.toURI().toURL();

            //jar路径加入到系统url路径里
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //回写访问权限
            method.setAccessible(accessible);
        }

    }

    @Test
    public void test2(){
        System.out.println((int)'\n');
        System.out.println((int)'\r');
    }
}
