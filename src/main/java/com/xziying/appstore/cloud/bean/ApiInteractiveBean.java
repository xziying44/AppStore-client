package com.xziying.appstore.cloud.bean;

import com.xziying.appstore.api.ApiInteractive;
import com.xziying.appstore.api.Impl.ApiInteractiveImpl;
import com.xziying.appstore.api.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * ApiInteractiveImpl
 *
 * @author : xziying
 * @create : 2021-04-05 13:28
 */
@Configuration
public class ApiInteractiveBean {

    @Resource
    Request request;

    @Bean
    ApiInteractive apiInteractive(){
        return new ApiInteractiveImpl(request);
    }
}
