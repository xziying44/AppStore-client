package com.xziying.appstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MainApplication 启动类
 *
 * @author : xziying
 * @create : 2021-01-22 22:53
 */

@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        System.out.println("服务器已启动成功！");
    }
}
