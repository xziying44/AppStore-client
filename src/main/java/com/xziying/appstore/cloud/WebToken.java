package com.xziying.appstore.cloud;

import com.xziying.appstore.plugIn.cloud.TokenCloud;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

/**
 * Token
 *
 * @author : xziying
 * @create : 2021-04-02 21:53
 */
@Component
public class WebToken implements TokenCloud {

    String token;

    int readCount = 0;  // 读取进程
    Semaphore mutex = new Semaphore(1); // readCount互斥信号
    Semaphore wrt = new Semaphore(1);   // 写进程互斥信号


    /**
     * 获取当前token
     */
    @Override
    public String getToken() throws InterruptedException {
        mutex.acquire();
        readCount++;
        if (readCount == 1) wrt.acquire();
        mutex.release();

        String temp = token;    // 读取资源

        mutex.acquire();
        readCount--;
        if (readCount == 0) wrt.release();
        mutex.release();

        return temp;

    }

    /**
     * 堵塞所有读取token线程，准备更新token
     */
    @Override
    public void readyToUpdate() throws InterruptedException {
        wrt.acquire();
    }

    /**
     * 完成token的更新，写入缓存，放行读取token线程
     * @param token 获取到的新token
     */
    @Override
    public void updateComplete(String token) {
        this.token = token;
        wrt.release();
    }
}
