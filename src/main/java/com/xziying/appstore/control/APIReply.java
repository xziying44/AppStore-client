package com.xziying.appstore.control;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * APIReply API请求回复
 *
 * @author : xziying
 * @create : 2021-01-24 19:57
 */
@Repository
public class APIReply {
    final int N = 50;
    final String[] data = new String[N];   // 数据区
    int in = 0;
    int out = 0;
    final Semaphore mutex = new Semaphore(1);    // 互斥信号量
    final Semaphore empty = new Semaphore(N);           //  缓冲区资源信号量
    final Semaphore full = new Semaphore(0);    //   满缓冲区资源信号量


    public void requestAPI(String json) throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        data[in] = json;
        in = (in + 1) % N;
        mutex.release();
        full.release();
    }


    public String obtainAPI() throws InterruptedException {
        String json;

        full.acquire();
        mutex.acquire();
        json = data[out];
        out = (out + 1) % N;
        mutex.release();
        empty.release();
        return json;
    }

}
