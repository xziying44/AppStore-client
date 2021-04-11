package com.xziying.appstore;

import com.xziying.appstore.utils.HttpDownloadUtil;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * HTTPTest
 *
 * @author : xziying
 * @create : 2021-04-04 11:32
 */
public class HTTPTest {
    @Test
    public void test1() throws MalformedURLException {
        System.out.println(HttpDownloadUtil.download("https://ceshi-1251371370.cos.ap-guangzhou.myqcloud.com/com.xziying.collectionAndForwarding.jar", "G:\\\\JavaProgram\\\\新锐项目\\\\打包项目\\\\app\\\\com.xziying.collectionAndForwarding.jar"));
    }
}
