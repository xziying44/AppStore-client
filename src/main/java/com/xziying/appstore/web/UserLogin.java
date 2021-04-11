package com.xziying.appstore.web;

import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.cloud.ConfSwitchCloud;
import com.xziying.appstore.cloud.WebToken;
import com.xziying.appstore.plugIn.PlugInDiscovery;
import com.xziying.appstore.plugIn.PluginPool;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserLogin
 *
 * @author : xziying
 * @create : 2021-04-03 19:49
 */
@RestController
@RequestMapping("/user")
public class UserLogin {

    @Resource
    PlugInDiscovery plugInDiscovery;

    @Resource
    PluginPool pluginPool;

    @Resource
    DatabaseService databaseService;

    @Resource
    WebToken webToken;

    @Resource
    ConfSwitchCloud confSwitchCloud;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(
            String account,
            String password
    ){
        String login = databaseService.login(account, password);
        System.out.println(login);
        JSONObject jsonObject = JSONObject.parseObject(login);
        if (jsonObject.get("code").equals(0)){
            // 登录成功
            try {
                webToken.readyToUpdate();
                String token = (String) jsonObject.get("token");
                webToken.updateComplete(token);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "{\"code\": -1, \"msg\": \"未知错误!\"}";
            }

        }
        return login;
    }

    /**
     * 登录后初始化
     */
    @ResponseBody
    @RequestMapping(value = "/initialization", method = RequestMethod.GET)
    public void initialization(){
        confSwitchCloud.refresh();
        pluginPool.loadAuthorizer();
        plugInDiscovery.scanPackage();
    }
}
