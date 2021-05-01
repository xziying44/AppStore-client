package com.xziying.appstore.authorize;

import com.xziying.appstore.api.DatabaseService;
import com.xziying.appstore.cloud.constant.Constant;
import com.xziying.appstore.cloud.gateway.VerificationCodeCloud;
import com.xziying.appstore.cloud.gateway.VerificationCodeConstructor;
import com.xziying.appstore.utils.RSAUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * CloudTest
 *
 * @author : xziying
 * @create : 2021-04-11 12:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudTest {

    @Resource
    DatabaseService databaseService;

    @Test
    public void test1(){

    }
}
