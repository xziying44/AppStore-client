package com.xziying.appstore.cloud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.plugIn.cloud.TokenCloud;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ConfSwitchCloud
 *
 * @author : xziying
 * @create : 2021-04-09 21:52
 */
public class ConfSwitchCloud {
    DatabaseService databaseService;    // 云端操作
    TokenCloud token;       // 登录后获取的token
    //List<? extends ConfSwitchNormal> confSwitchNormals = new ArrayList<>();
    List<JSONObject> confSwitchNormals = new ArrayList<>();

    public ConfSwitchCloud(DatabaseService databaseService, TokenCloud token) {
        this.databaseService = databaseService;
        this.token = token;
    }

    /**
     * 刷新配置库
     */
    @SuppressWarnings("unchecked")
    public void refresh(){
        try {
            JSONObject jsonObject = JSONObject.parseObject(databaseService.querySwitch(token.getToken()));
            Object confSwitches = jsonObject.get("confSwitches");
            confSwitchNormals = (List<JSONObject>)confSwitches;
            System.out.println(confSwitches);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getConfigByClazz(String clazz){
        for (JSONObject object : confSwitchNormals){
            if (clazz.equals(object.get("clazz"))){
                return object;
            }
        }
        return null;
    }


    public int update(String clazz, int weight, int state){
        try {
            return databaseService.updateSwitch(token.getToken(), clazz, weight, state);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        } finally {
            refresh();
        }
    }

}
