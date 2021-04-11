package com.xziying.appstore;

import com.xziying.appstore.api.DatabaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * UserTest
 *
 * @author : xziying
 * @create : 2021-04-03 20:38
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {
    @Resource
    DatabaseService databaseService;

    @Test
    public void test1(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwiaWF0IjoxNjE3NDUzMzkxLCJleHAiOjE2MTc0Njc3OTF9.C22e21OqZCXb2lv2W41747XCvUPW6_kOsrz7b1gIZqk";
        System.out.println(databaseService.getPluginList(token));
    }

    @Test
    public void test2(){
        String aaa = databaseService.test("aaa");
        System.out.println(aaa);
    }
}
