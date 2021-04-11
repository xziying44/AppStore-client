package com.xziying.appstore.cloud;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.plugIn.cloud.PluginConfigCloud;
import com.xziying.appstore.plugIn.cloud.TokenCloud;

import java.util.Map;

/**
 * PluginConfigCloudImpl
 *
 * @author : xziying
 * @create : 2021-04-02 22:28
 */
public class PluginConfigCloudImpl implements PluginConfigCloud {
    DatabaseService databaseService;    // 云端操作
    TokenCloud token;       // 登录后获取的token
    int pid;            // 插件id
    String fromQQ;      // 所属QQ
    Map<String, String> map;    //

    public PluginConfigCloudImpl(DatabaseService databaseService, TokenCloud token, int pid, String fromQQ) throws InterruptedException {
        this.databaseService = databaseService;
        this.token = token;
        this.pid = pid;
        this.fromQQ = fromQQ;
        this.map = databaseService.queryConfig(token.getToken(), pid, fromQQ);
    }

    @Override
    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public void write(String key, String value) throws InterruptedException {
        if (databaseService.updateConfig(token.getToken(), pid, fromQQ, key, value) == 0){
            map.put(key, value);
        }
    }

    @Override
    public String read(String key){
        return map.get(key);
    }
}
