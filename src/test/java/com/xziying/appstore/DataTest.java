package com.xziying.appstore;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.cloud.DatabaseGatewayImpl;
import com.xziying.appstore.cloud.WebToken;
import com.xziying.appstore.plugIn.cloud.DatabaseCloud;
import com.xziying.appstore.plugIn.cloud.DatabaseGateway;
import com.xziying.appstore.plugIn.domain.data.DataMap;
import com.xziying.appstore.plugIn.domain.data.DataObject;
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

    @Resource
    DatabaseService databaseService;




    @Test
    public void test1() throws InterruptedException {
        JSONObject jsonObject = JSONObject.parseObject(databaseService.login("xziying", "12345"));
        String token = jsonObject.getObject("token", String.class);
        webToken.readyToUpdate();
        webToken.updateComplete(token);
        DatabaseGateway databaseGateway = new DatabaseGatewayImpl(databaseService, webToken, "com.xziying.collectionAndForwarding");
        DatabaseCloud databaseCloudAll = databaseGateway.get("all");


        DataObject temp;
        DataObject o = databaseCloudAll.read("配置");
        DataMap dataMap;
        if (!(o instanceof DataMap)){
            dataMap = databaseCloudAll.write("配置");
        } else {
            dataMap = (DataMap) o;
        }

        // 采集目标群
        String collectionTargetGroup = "1111111@2222222@3333333@4444444";
        temp = dataMap.get("采集目标群");

        if (temp == null || !temp.getValue().equals(collectionTargetGroup)){
            dataMap.put("采集目标群", collectionTargetGroup);
        }
        jsonObject = new JSONObject();
        jsonObject.put("data", dataMap);
        System.out.println(jsonObject.toJSONString());
    }

}
