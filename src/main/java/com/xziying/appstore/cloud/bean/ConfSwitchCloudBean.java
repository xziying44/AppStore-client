package com.xziying.appstore.cloud.bean;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.cloud.ConfSwitchCloud;
import com.xziying.appstore.cloud.WebToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * ConfSwitchCloudBean
 *
 * @author : xziying
 * @create : 2021-04-09 22:00
 */
@Configuration
public class ConfSwitchCloudBean {

    @Resource
    DatabaseService databaseService;

    @Resource
    WebToken webToken;

    @Bean
    ConfSwitchCloud confSwitchCloud(){
        return new ConfSwitchCloud(databaseService, webToken);
    }
}
