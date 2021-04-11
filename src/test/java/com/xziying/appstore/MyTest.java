package com.xziying.appstore;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.proxy.ProxyPattern;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    public String doPost(String URL){
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        try{
            java.net.URL url = new URL(URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            //发送POST请求必须设置为true
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //设置连接超时时间和读取超时时间
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //获取输出流
            out = new OutputStreamWriter(conn.getOutputStream());
            String jsonStr = "{\"qry_by\":\"name\", \"name\":\"Tim\"}";
            out.write(jsonStr);
            out.flush();
            out.close();
            //取得输入流，并使用Reader读取
            if (200 == conn.getResponseCode()){
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = in.readLine()) != null){
                    result.append(line);
                    System.out.println(line);
                }
            }else{
                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(out != null){
                    out.close();
                }
                if(in != null){
                    in.close();
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
        return result.toString();
    }
    public String doGet(String URL){
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder result = new StringBuilder();
        try{
            //创建远程url连接对象
            URL url = new URL(URL);
            //通过远程url连接对象打开一个连接，强转成HTTPURLConnection类
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            //设置连接超时时间和读取超时时间
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("Accept", "application/json");
            //发送请求
            conn.connect();
            //通过conn取得输入流，并使用Reader读取
            if (200 == conn.getResponseCode()){
                is = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null){
                    result.append(line);
                    System.out.println(line);
                }
            }else{
                System.out.println("ResponseCode is an error code:" + conn.getResponseCode());
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try{
                if(br != null){
                    br.close();
                }
                if(is != null){
                    is.close();
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
            conn.disconnect();
        }
        return result.toString();
    }


    @Test
    public void test1(){
        DatabaseService databaseService = (DatabaseService) ProxyPattern.getProxy(DatabaseService.class, "129.204.9.203", 7799);
        ExecutorService pool = Executors.newFixedThreadPool(20);


        for (int i = 0; i < 10; i++) {
            int finalI = i;
            pool.execute(() -> {
                long a = System.currentTimeMillis();
                System.out.println(databaseService.queryConfig("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNjE3Mjc0MTY2LCJleHAiOjE2MTcyODg1NjZ9.mVNMzhH7v9-RbIe2XS0Ja0uYuVT4-YdjGvd1H5j1F0g", 1, "all"));
                long b = System.currentTimeMillis();
                System.out.println("处理时间：" + (b - a) + "ms");
                System.out.println(finalI);
            });
        }
        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test2(){
        DatabaseService databaseService = (DatabaseService) ProxyPattern.getProxy(DatabaseService.class, "129.204.9.203", 7799);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNjE3Mjc0NDQyLCJleHAiOjE2MTcyODg4NDJ9.xL3jwuM5_gMtWqYjN0qKV4dqaPZDLLAaIWqNaz274Mw";
        long a = System.currentTimeMillis();
        Map<String, String> map = databaseService.queryConfig(token, 2, "all");
        System.out.println(map);
        long b = System.currentTimeMillis();
        System.out.println("处理时间：" + (b - a) + "ms");

    }

    @Test
    public void test3(){
        DatabaseService databaseService = (DatabaseService) ProxyPattern.getProxy(DatabaseService.class, "129.204.9.203", 7799);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNjE3Mjc0NDQyLCJleHAiOjE2MTcyODg4NDJ9.xL3jwuM5_gMtWqYjN0qKV4dqaPZDLLAaIWqNaz274Mw";

        long a = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            System.out.println(databaseService.updateConfig(token, 3, "all", "key" + i, "value" + i));
            System.out.println(i);
        }

        long b = System.currentTimeMillis();
        System.out.println("处理时间：" + (b - a) + "ms");

    }

    @Test
    public void test4(){
        String tokenStr = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNjE3MzcxNTExLCJleHAiOjE2MTczODU5MTF9.ruKKjdzRzBqlCiSuyte6Lpy1rx9Z7iyYVvCiB-AEZKI";

    }
}
