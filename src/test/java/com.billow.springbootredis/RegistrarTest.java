package com.billow.springbootredis;

import com.billow.springbootredis.custom.CustomService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liuyongtao
 * @create 2020-10-30 14:50
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class RegistrarTest {

    @Autowired
    public CustomService customService;

    @Test
    public void test(){
        customService.reset();
    }
}
