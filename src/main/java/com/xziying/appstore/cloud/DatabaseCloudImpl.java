package com.xziying.appstore.cloud;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.plugIn.cloud.DatabaseCloud;
import com.xziying.appstore.plugIn.cloud.TokenCloud;
import com.xziying.appstore.plugIn.domain.data.DataMap;
import com.xziying.appstore.plugIn.domain.data.DataObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PluginConfigCloudImpl
 *
 * @author : xziying
 * @create : 2021-04-02 22:28
 */
public class DatabaseCloudImpl implements DatabaseCloud {
    DatabaseService databaseService;    // 云端操作
    TokenCloud token;       // 登录后获取的token
    int dcid;               // 数据库id
    String json;            // 原始Json
    Map<String, DataObject> map = new ConcurrentHashMap<>();    //

    public DatabaseCloudImpl(DatabaseService databaseService, TokenCloud token, int dcid, String json) throws InterruptedException {
        this.databaseService = databaseService;
        this.token = token;
        this.dcid = dcid;
        this.json = json;
        processJson();
    }

    /**
     * 处理Json数据
     */
    private void processJson(){
        JSONObject jsonObject = JSONObject.parseObject(json);
        List<Object> list = jsonObject.getObject("userConfigs", JSONArray.class);
        // 扫描对象实例
        for (Object o : list){
            if (o instanceof JSONObject) {
                JSONObject obj = (JSONObject) o;
                if (!obj.getObject("noobj", Boolean.class)){
                    // 对象
                    String key = obj.getObject("key", String.class);
                    DataMap dataMap = new DataMap(
                            obj.getObject("duid", Integer.class),
                            key,
                            obj.getObject("value", String.class),
                            new ConcurrentHashMap<>(),
                            this
                    );
                    map.put(key, dataMap);
                }
            }
        }
        // 封装非对象实例
        for (Object o : list){
            if (o instanceof JSONObject){
                JSONObject obj = (JSONObject) o;
                if (obj.getObject("noobj", Boolean.class)){
                    // 非对象
                    String key = obj.getObject("key", String.class);
                    DataObject dataObject = new DataObject(
                            obj.getObject("duid", Integer.class),
                            key,
                            obj.getObject("value", String.class)
                    );

                    String[] split = key.split("\\.");
                    // 判断是否存在子对象
                    if (split.length != 1){
                        if (map.containsKey(split[0])){
                            // 存在时
                            StringBuilder tKey = new StringBuilder();
                            for (int i = 1; i < split.length; i++) {
                                tKey.append(split[i]);
                            }
                            ((DataMap)map.get(split[0])).getMap().put(tKey.toString(), dataObject);
                        }
                    } else {
                        map.put(key, dataObject);
                    }

                }
            }
        }
    }

    @Override
    public Map<String, DataObject> getMap() {
        return map;
    }

    @Override
    public DataObject write(String key, String value) throws InterruptedException {
        JSONObject jsonObject;
        if (map.containsKey(key)){
            // 存在时
            DataObject dataObject = map.get(key);
            dataObject.setValue(value);
            jsonObject = JSONObject.parseObject(databaseService.updateDatabase(token.getToken(), dataObject.getDuid(), dcid,
                    dataObject.getKey(), value, !(dataObject instanceof DataMap)));
            if (jsonObject.getObject("code", int.class) == 0){
                return dataObject;
            }
        } else {
            // 不存在时
            jsonObject = JSONObject.parseObject(databaseService.updateDatabase(token.getToken(), -1, dcid,
                    key, value, true));
            if (jsonObject.getObject("code", int.class) == 0){
                JSONObject userConfig = jsonObject.getObject("userConfig", JSONObject.class);
                return new DataObject(
                        userConfig.getObject("duid", int.class),
                        userConfig.getObject("key", String.class),
                        userConfig.getObject("value", String.class)
                );
            }
        }
        return null;
    }

    @Override
    public DataMap write(String key) throws InterruptedException {
        if (!map.containsKey(key)){
            JSONObject jsonObject = JSONObject.parseObject(databaseService.updateDatabase(token.getToken(), -1, dcid,
                    key, "", false));
            if (jsonObject.getObject("code", int.class) == 0){
                JSONObject userConfig = jsonObject.getObject("userConfig", JSONObject.class);
                return new DataMap(
                        userConfig.getObject("duid", int.class),
                        userConfig.getObject("key", String.class),
                        userConfig.getObject("value", String.class),
                        new ConcurrentHashMap<>(),
                        this
                );
            }
        }
        return (DataMap) map.get(key);
    }

    @Override
    public DataObject read(String key){
        return map.get(key);
    }

    @Override
    public boolean remove(String key) {
        try {
            if (map.containsKey(key)){
                DataObject dataObject = map.get(key);
                return databaseService.delRecording(token.getToken(), dataObject.getDuid()) == 0;
            }
            return false;
        } catch (Throwable e){
            return false;
        }
    }
}
