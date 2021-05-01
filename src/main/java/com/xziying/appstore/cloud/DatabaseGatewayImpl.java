package com.xziying.appstore.cloud;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.plugIn.cloud.DatabaseCloud;
import com.xziying.appstore.plugIn.cloud.DatabaseGateway;
import com.xziying.appstore.plugIn.cloud.TokenCloud;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DatabaseGatewayImpl
 *
 * @author : xziying
 * @create : 2021-04-19 21:34
 */
public class DatabaseGatewayImpl implements DatabaseGateway {
    DatabaseService databaseService;    // 云端操作
    TokenCloud token;       // 登录后获取的token
    String clazz;            // 插件

    Map<String, DatabaseCloud> dataBaseMap = new ConcurrentHashMap<>();


    public DatabaseGatewayImpl(DatabaseService databaseService, TokenCloud token, String clazz) throws InterruptedException {
        this.databaseService = databaseService;
        this.token = token;
        this.clazz = clazz;
        String database = databaseService.getDatabase(token.getToken(), clazz);
        List<Object> list = JSONObject.parseObject(database).getObject("database", JSONArray.class);
        for (Object obj: list){
            if (obj instanceof JSONObject){
                JSONObject temp = (JSONObject) obj;
                // 获取网络数据库
                Integer dcid = temp.getObject("dcid", Integer.class);
                String dc = databaseService.queryDatabase(token.getToken(), dcid);
                DatabaseCloud databaseCloud = new DatabaseCloudImpl(databaseService, token, dcid, dc);
                dataBaseMap.put(temp.getObject("fromQQ", String.class), databaseCloud);
            }
        }
    }

    @Override
    public Map<String, DatabaseCloud> getDataBaseMap() {
        return dataBaseMap;
    }

    @Override
    public int createDatabase(String fromQQ) {
        try {
            return databaseService.createDatabase(token.getToken(), clazz, fromQQ);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int delDataBase(String fromQQ) {
        try {
            return databaseService.delDatabase(token.getToken(), clazz, fromQQ);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public DatabaseCloud get(String fromQQ) {
        if (fromQQ.equals("all") && !dataBaseMap.containsKey(fromQQ)){
            // 创建新的数据库
            createDatabase(fromQQ);
        }
        return dataBaseMap.get(fromQQ);
    }
}
