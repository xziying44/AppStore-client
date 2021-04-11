package com.xziying.appstore.control;

import com.alibaba.fastjson.JSONObject;
import com.xziying.appstore.api.Request;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * BufferZone 主动API缓冲区
 *
 * @author : xziying
 * @create : 2021-01-24 15:31
 */
@Repository
public class APIRequest implements Request {
    final int N = 50;
    final String[] data = new String[N];   // 数据区
    int in = 0;
    int out = 0;
    final Semaphore mutex = new Semaphore(1);    // 互斥信号量
    final Semaphore empty = new Semaphore(N);           //  缓冲区资源信号量
    final Semaphore full = new Semaphore(0);    //   满缓冲区资源信号量

    @Resource
    APIReply apiReply;  // 回复区


    /**
     * 申请API请求 推入缓冲区
     * @param json Json信息
     */
    @Override
    public void requestAPI(String json) throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        data[in] = json;
        in = (in + 1) % N;
        mutex.release();
        full.release();
    }

    /**
     * 获取缓冲区的API请求
     * @return Json信息
     */
    @Override
    public String obtainAPI() throws InterruptedException {
        String json;

        full.acquire();
        mutex.acquire();
        json = data[out];
        mutex.release();
        full.release();
        return json;
    }

    /**
     * API请求处理完成，从缓冲区移除
     */
    @Override
    public void completeAPI(String reply) throws InterruptedException {
        full.acquire();
        mutex.acquire();
        out = (out + 1) % N;
        mutex.release();
        empty.release();
        apiReply.requestAPI(reply);
    }
    @Override
    public String getReply() throws InterruptedException {
        return apiReply.obtainAPI();
    }

    /**
     * 封装API 为Json
     * @return Json文本
     */
    @Override
    public String packageAPI(
            String replyQQ,
            String apiName,
            Object[] args
    ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("replyQQ", replyQQ);
        jsonObject.put("apiName", apiName);
        jsonObject.put("args", args);
        String check = UUID.randomUUID().toString().replaceAll("","").toUpperCase();
        jsonObject.put("check", check);
        return jsonObject.toString();
    }
}
