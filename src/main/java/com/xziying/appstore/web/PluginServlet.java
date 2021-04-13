package com.xziying.appstore.web;

import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.cloud.WebToken;
import com.xziying.appstore.plugIn.PlugInDiscovery;
import com.xziying.appstore.plugIn.PluginPool;
import com.xziying.appstore.plugIn.ProtocolEntry;
import com.xziying.appstore.plugIn.cloud.PluginConfigCloud;
import com.xziying.appstore.plugIn.domain.PlugInInfo;
import com.xziying.appstore.plugIn.pack.ProtocolEntryInfo;
import com.xziying.appstore.utils.HttpDownloadUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PluginServlet
 *
 * @author : xziying
 * @create : 2021-04-03 20:29
 */
@RestController
@RequestMapping("/pluginServlet")
public class PluginServlet {

    @Resource
    DatabaseService databaseService;

    @Resource
    WebToken webToken;

    @Resource
    PlugInDiscovery plugInDiscovery;


    @Resource
    PluginPool pluginPool;

    @ResponseBody
    @RequestMapping("/getList")
    public String getList(){
        try {
            System.out.println(webToken.getToken());
            return databaseService.getPluginList(webToken.getToken());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "{\"code\" :  -1, \"msg\" : \"未知错误\"}";
    }

    @ResponseBody
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public String download(
            String clazz,
            String url
    ){
        String pathStr = plugInDiscovery.getPathStr();
        if (HttpDownloadUtil.download(url, pathStr + "\\" + clazz + ".jar")){
            return "{\"code\" :  0, \"msg\" :  \"下载成功\"}";
        }
        return "{\"code\" :  -1, \"msg\" :  \"下载失败\"}";
    }

    @ResponseBody
    @RequestMapping(value = "/load", method = RequestMethod.POST)
    public String load(
            String clazz
    ){
        String pathStr = plugInDiscovery.getPathStr();

        try {
            pluginPool.startPlugIn(clazz, pathStr + "\\" + clazz + ".jar");
            return "{\"code\" :  0, \"msg\" :  \"已安装\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"code\" :  -1, \"msg\" :  \"加载失败\"}";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getLocalList", method = RequestMethod.GET)
    public String getLocalList(){

        List<ProtocolEntryInfo> pluginList = pluginPool.getPluginList();
        List<String> error = new ArrayList<>();
        for (ProtocolEntryInfo protocolEntryInfo : pluginList){
            try {
                ProtocolEntry protocolEntry = protocolEntryInfo.getProtocolEntry();
                PlugInInfo info = protocolEntry.getInfo();
                info.setClazz(protocolEntryInfo.getClazz());
            } catch (Throwable e){
                error.add(protocolEntryInfo.getClazz());
                e.printStackTrace();
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", pluginList);
        jsonObject.put("error", error);
        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/refreshThePlugin", method = RequestMethod.GET)
    public void refreshThePlugin(){
        plugInDiscovery.scanPackage();
    }

    @ResponseBody
    @RequestMapping(value = "/adjustPriority", method = RequestMethod.POST)
    public String adjustPriority(
            String clazz,
            int behavior
    ){
        JSONObject jsonObject = new JSONObject();
        if (pluginPool.swapWeight(clazz, behavior)){
            jsonObject.put("code", 0);
        } else {
            jsonObject.put("code", -1);
            jsonObject.put("msg", "操作失败!");
        }
        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/switchPlugIn", method = RequestMethod.POST)
    public String switchPlugIn(
            String clazz,
            int behavior
    ){
        JSONObject jsonObject = new JSONObject();
        if (pluginPool.switchPlugIn(clazz, behavior)){
            jsonObject.put("code", 0);
        } else {
            jsonObject.put("code", -1);
            jsonObject.put("msg", "操作失败!");
        }
        return jsonObject.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/getVerification", method = RequestMethod.GET)
    public String getVerification(
    ){
        return pluginPool.getVerification();
    }
}
