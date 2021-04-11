package com.xziying.appstore.plugIn;

import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.abnormal.PlugInDoesNotExistException;
import com.xziying.appstore.annotation.ApiHttpRequest;
import com.xziying.appstore.api.ApiInteractive;
import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.api.Impl.ApiInteractiveImpl;
import com.xziying.appstore.api.Request;
import com.xziying.appstore.cloud.ConfSwitchCloud;
import com.xziying.appstore.cloud.PluginConfigCloudImpl;
import com.xziying.appstore.cloud.WebToken;
import com.xziying.appstore.cloud.constant.Constant;
import com.xziying.appstore.cloud.gateway.VerificationCodeCloud;
import com.xziying.appstore.cloud.gateway.VerificationCodeConstructor;
import com.xziying.appstore.control.APIReply;
import com.xziying.appstore.http.ParameterMap;
import com.xziying.appstore.plugIn.cloud.PluginConfigCloud;
import com.xziying.appstore.plugIn.domain.EventInfo;
import com.xziying.appstore.plugIn.pack.ProtocolEntryInfo;
import com.xziying.appstore.utils.RSAUtil;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PluginPool 插件池，管理中心
 *
 * @author : xziying
 * @create : 2021-01-25 17:22
 */
@Repository
public class PluginPool {

    /**
     * 类名map
     */
    Map<String, ProtocolEntryInfo> pluginList = new ConcurrentHashMap<>();

    /**
     * 权值排序
     */
    List<ProtocolEntryInfo> pluginWeight = new CopyOnWriteArrayList<>();

    /**
     * 索引
     */
    Set<Integer> pluginIndex = new TreeSet<>();


    @Resource
    ApiInteractive apiInteractive;

    @Resource
    DatabaseService databaseService;

    @Resource
    ConfSwitchCloud confSwitchCloud;

    @Resource
    WebToken webToken;

    private VerificationCodeCloud verificationCodeCloud;

    @Deprecated
    private void loadJar(String jarPath) throws Exception {
        File jarFile = new File(jarPath); // 从URLClassLoader类中获取类所在文件夹的方法，jar也可以认为是一个文件夹

        if (!jarFile.exists()) {
            System.out.println("jar file not found.");
            return;
        }

        //获取类加载器的addURL方法，准备动态调用
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
            return;
        }

        // 获取方法的访问权限，保存原始值
        boolean accessible = method.isAccessible();
        try {
            //修改访问权限为可写
            if (!accessible) {
                method.setAccessible(true);
            }

            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();

            //获取jar文件的url路径
            java.net.URL url = jarFile.toURI().toURL();

            //jar路径加入到系统url路径里
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //回写访问权限
            method.setAccessible(accessible);
        }

    }

    public void loadAuthorizer(){
        try {
            byte[] verificationCodeCloud = databaseService.getVerificationCodeCloud();
            byte[] decrypt = RSAUtil.decryptLong(verificationCodeCloud, Constant.DES_KEY);
            VerificationCodeConstructor classLoader = new VerificationCodeConstructor();
            Class<?> aClass = classLoader.loadClass(Constant.DES_CLAZZ,
                    decrypt, 0, decrypt.length);
            this.verificationCodeCloud = (VerificationCodeCloud) aClass.newInstance();
            this.verificationCodeCloud.initialize(databaseService, webToken.getToken());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public synchronized void startPlugIn(String name, String file) throws Exception {
        File path = new File(file);
        URL url= path.toURI().toURL();//将File类型转为URL类型，file为jar包路径
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {url});
        Class<?> plugIn = urlClassLoader.loadClass(name + ".Collection");


        Constructor<?> declaredConstructor = plugIn.getDeclaredConstructor(ApiInteractive.class, PluginConfigCloud.class);
        declaredConstructor.setAccessible(true);

        Integer pluginPic = databaseService.getPluginPic(name);
        if (!pluginPic.equals(-1)) {
            PluginConfigCloud pluginConfigCloud = new PluginConfigCloudImpl(databaseService, webToken, pluginPic, "all");

            ProtocolEntry protocolEntry = (ProtocolEntry) declaredConstructor.newInstance(apiInteractive, pluginConfigCloud);
            // 查询网络插件配置
            JSONObject configByClazz = confSwitchCloud.getConfigByClazz(name);
            if (configByClazz == null){
                // 创建新的配置
                if (confSwitchCloud.update(name, getIndex(), Constant.SWITCH_ON) == 0){
                    configByClazz = confSwitchCloud.getConfigByClazz(name);
                } else {
                    throw new PlugInDoesNotExistException();
                }
            }
            ProtocolEntryInfo protocolEntryInfo = new ProtocolEntryInfo(
                    protocolEntry, configByClazz.getString("clazz"),
                    Integer.parseInt(configByClazz.getString("weight")), Integer.parseInt(configByClazz.getString("state"))
            );
            // 加入池子
            pluginList.put(name, protocolEntryInfo);    // 加入类名池
            pluginWeight.add(protocolEntryInfo);        // 加入排序池
            pluginIndex.add(protocolEntryInfo.getWeight());  // 加入索引

            // 开始排序
            redefineTheOrder();
        } else {
            throw new PlugInDoesNotExistException();
        }
    }

    private Integer getIndex(){
        int i = 0;
        System.out.println(pluginIndex);
        while (true){
            if (!pluginIndex.contains(i)){
                return i;
            }
            i++;
        }
    }

    /**
     * 开关插件
     * @param clazz 类名
     * @param behavior 1开启 0关闭
     */
    public synchronized boolean switchPlugIn(String clazz, int behavior){
        try {
            ProtocolEntryInfo protocolEntryInfo = pluginList.get(clazz);
            if (confSwitchCloud.update(clazz, protocolEntryInfo.getWeight(), behavior) == 0){
                ProtocolEntryInfo temp = pluginList.get(clazz);
                temp.setState(behavior);
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 对插件优先级调整
     * @param clazz 类名
     * @param behavior  行为 1为上移，0为下移
     */
    public synchronized boolean swapWeight(String clazz, int behavior){
        try {
            if (behavior == 1 && pluginWeight.size() > 1) {
                // 上移事件
                for (int i = 1; i < pluginWeight.size(); i++) {
                    ProtocolEntryInfo index = pluginWeight.get(i);
                    if (index.getClazz().equals(clazz)) {
                        ProtocolEntryInfo exchange = pluginWeight.get(i - 1);
                        if (databaseService.swapSwitch(
                                webToken.getToken(),
                                index.getClazz(), exchange.getWeight(),
                                exchange.getClazz(), index.getWeight()
                        ) == 0){
                            int temp = index.getWeight();
                            index.setWeight(exchange.getWeight());
                            exchange.setWeight(temp);
                            redefineTheOrder();
                            confSwitchCloud.refresh();
                            return true;
                        }
                    }
                }
            } else if (behavior == 0 && pluginWeight.size() > 1){
                // 下移事件
                for (int i = 0; i < pluginWeight.size() - 1; i++) {
                    ProtocolEntryInfo index = pluginWeight.get(i);
                    if (index.getClazz().equals(clazz)) {
                        ProtocolEntryInfo exchange = pluginWeight.get(i + 1);
                        if (databaseService.swapSwitch(
                                webToken.getToken(),
                                index.getClazz(), exchange.getWeight(),
                                exchange.getClazz(), index.getWeight()
                        ) == 0){
                            int temp = index.getWeight();
                            index.setWeight(exchange.getWeight());
                            exchange.setWeight(temp);
                            redefineTheOrder();
                            confSwitchCloud.refresh();
                            return true;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清楚所有加载的插件
     */
    public synchronized void clear(){
        pluginList.clear();
        pluginWeight.clear();
    }

    /**
     * 重定义排序插件名
     */
    private void redefineTheOrder(){
        pluginWeight.sort(Comparator.comparingInt(ProtocolEntryInfo::getWeight));
    }

    public List<ProtocolEntryInfo> getPluginList(){
        return pluginWeight;
    }

    public Map<String, ProtocolEntryInfo> getPluginMap() {
        return pluginList;
    }

    public byte[] getHTMLByPluginName(String name, String url){
        try {
            System.out.println(name);
            System.out.println(pluginList.get(name));
            ProtocolEntry protocolEntry = pluginList.get(name).getProtocolEntry();
            return protocolEntry.getConfigHTML(url);
        } catch (Exception e){
            e.printStackTrace();
            return new byte[0];
        }

    }

    public byte[] getApiHttpRequest(String name, String apiName, Map<String, String[]> parameterMap){
        String reply = "";
        try {
            ProtocolEntry protocolEntry = pluginList.get(name).getProtocolEntry();
            Method[] methods = protocolEntry.getClass().getMethods();
            Method selected = null;
            for (Method method : methods){
                ApiHttpRequest annotation = method.getAnnotation(ApiHttpRequest.class);
                if (annotation!=null && annotation.value().equals(apiName)){
                    selected = method;
                    break;
                }
            }
            if (selected == null){
                reply = "找不到API";
            } else {
                reply = (String) selected.invoke(protocolEntry, new ParameterMap(parameterMap));
            }
        } catch (Exception e){
            e.printStackTrace();
            reply = "未知错误";
        }
        char[] chars = reply.toCharArray();
        Charset cs = StandardCharsets.UTF_8;
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }


    public void eventHandling(
            EventInfo eventInfo
    ){
        for (ProtocolEntryInfo protocolEntryInfo : pluginWeight){
            try {
                if (protocolEntryInfo.getState() == Constant.SWITCH_OFF){
                    continue;
                }
                ProtocolEntry protocolEntry = protocolEntryInfo.getProtocolEntry();
                int event = this.verificationCodeCloud.processTheMessage(protocolEntry, eventInfo);
                if (event == 1){
                    break;
                }
            } catch (Throwable throwable){
                System.out.println("异常 -> " + protocolEntryInfo.getClazz() + "插件处理事件方法调用失败！");
            }
        }

    }
}
