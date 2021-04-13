package com.xziying.appstore.cloud.bean;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.proxy.ProxyPattern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DatabaseServiceBean
 *
 * @author : xziying
 * @create : 2021-04-03 19:49
 */
@Configuration
public class DatabaseServiceBean {

    @Bean
    DatabaseService databaseService(){
        return (DatabaseService) ProxyPattern.getProxy(DatabaseService.class, "127.0.0.1", 7799);
        //return (DatabaseService) ProxyPattern.getProxy(DatabaseService.class, "app.xrxrxr.com", 7799);
    }
}
