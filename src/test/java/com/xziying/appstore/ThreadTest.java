package com.xziying.appstore;

import com.xziying.appstore.cloud.WebToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * ThreadTest
 *
 * @author : xziying
 * @create : 2021-04-02 22:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadTest {

    @Resource
    WebToken webToken;

    @Test
    public void test1() throws InterruptedException {
        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println("读" + i + " -> " + webToken.getToken());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    webToken.readyToUpdate();
                    Thread.sleep(100);
                    webToken.updateComplete("" + i + "token");
                    System.out.println("写" + i);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();


        Thread.sleep(60*1000);
    }
}
