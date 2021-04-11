package com.xziying.appstore;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.cloud.PluginConfigCloudImpl;
import com.xziying.appstore.cloud.WebToken;
import com.xziying.appstore.plugIn.cloud.PluginConfigCloud;
import com.xziying.appstore.proxy.ProxyPattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * DataTest
 *
 * @author : xziying
 * @create : 2021-04-02 22:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataTest {
    @Resource
    WebToken webToken;


    @Test
    public void test1() throws InterruptedException {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNjE3NDUwMzQ4LCJleHAiOjE2MTc0NjQ3NDh9.85UW-tSmXjwbpSilpUEBwkWSTgzJmwASGpMpKSg8LLo";
        DatabaseService databaseService = (DatabaseService) ProxyPattern.getProxy(DatabaseService.class, "app.xrxrxr.com", 7799);


        webToken.readyToUpdate();
        webToken.updateComplete(token);

        PluginConfigCloud pluginConfigCloud = new PluginConfigCloudImpl(databaseService, webToken, 1, "all");

        /*System.out.println(pluginConfigCloud.read("key"));
        pluginConfigCloud.write("测试键", "测试值");*/
        System.out.println(pluginConfigCloud.read("测试键"));
    }
}
