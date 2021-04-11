package com.xziying.appstore.web;

import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.plugIn.PlugInDiscovery;
import com.xziying.appstore.plugIn.PluginPool;
import com.xziying.appstore.plugIn.ProtocolEntry;
import com.xziying.appstore.plugIn.domain.PlugInInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * PlugInRelated
 *
 * @author : xziying
 * @create : 2021-03-21 17:00
 */
@RestController
@RequestMapping("/plugin")
public class PlugInRelated {
    @Resource
    PluginPool pluginPool;

    @Resource
    PlugInDiscovery plugInDiscovery;

/*    @ResponseBody
    @RequestMapping("/getList")
    public String getList(){
        JSONObject jsonObject = new JSONObject();
        Map<String, ProtocolEntry> pluginList = pluginPool.getPluginList();
        Set<String> strings = pluginList.keySet();
        Map<String, PlugInInfo> plugInInfoMap = new HashMap<>();

        for (String pluginName : strings){
            ProtocolEntry protocolEntry = pluginList.get(pluginName);
            plugInInfoMap.put(pluginName, protocolEntry.getInfo());
        }

        jsonObject.put("info", plugInInfoMap);
        return jsonObject.toString();
    }*/

/*    @ResponseBody
    @RequestMapping("/refresh")
    public String refresh(){
        plugInDiscovery.scanPackage();
        return getList();
    }*/
}
