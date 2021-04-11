package com.xziying.appstore.plugIn;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;

/**
 * PlugInDiscovery 插件发现类
 *
 * @author : xziying
 * @create : 2021-03-21 16:24
 */

@Repository
public class PlugInDiscovery {


    @Resource
    PluginPool pluginPool;


    public String getPathStr(){
        File path = getPath();
        return path.getPath();
    }

    public File getPath(){
        File dir = new File(System.getProperty("user.dir") + "\\app");
        if( dir.exists()){
            // app 目录存在时
            return dir;
        } else {
            dir = new File("G:\\JavaProgram\\新锐项目\\打包项目\\app");
            return dir;
        }
    }

    public void scanPackage(){
        pluginPool.clear();
        searchFile(getPath());
    }

    private void searchFile(File dir){
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                    String castles = fileName.substring(0,fileName.lastIndexOf("."));
                    if (suffix.equals("jar")){
                        // 确定为jar文件
                        try {
                            pluginPool.startPlugIn(castles, file.getPath());
                            System.out.println("已加载 -> " + castles);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("失败 -> " + castles);
                        }
                    }
                }
            }
        }
    }


    /*@PostConstruct
    public void initialize(){
        scanPackage();
    }*/
}
