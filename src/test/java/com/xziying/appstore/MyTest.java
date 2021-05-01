package com.xziying.appstore;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.proxy.ProxyPattern;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * MyTest
 *
 * @author : xziying
 * @create : 2021-03-29 20:37
 */
public class MyTest {
   @Test
    public void test1(){
       String s = "Ma";
       String[] split = s.split("\\.");
       System.out.println(Arrays.toString(split));
   }
}
