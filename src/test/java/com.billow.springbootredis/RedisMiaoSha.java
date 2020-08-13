package com.billow.springbootredis;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 秒杀
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class RedisMiaoSha {

    Logger logger = LoggerFactory.getLogger(RedisMiaoSha.class);

    public static final String KEY = "integer_key";

    //    @Autowired
//    private RedisTemplate<String,Integer> redisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplateObj;

    ValueOperations<String, Object> opsForValue;

    @Before
    public void before() {
        opsForValue = redisTemplateObj.opsForValue();
        opsForValue.set(KEY, 10);
    }

    @Test
    public void test() {

        ExecutorService service = Executors.newFixedThreadPool(200);

        for (int i = 0; i < 2; i++) {
            service.execute(() -> {
                this.miaoSha();
            });
        }

    }


    public void miaoSha() {
        Integer count = (Integer) opsForValue.get(KEY);
        if (count == 0) {
            logger.info("====>>>> 没有了...");
            return;
        }
        redisTemplateObj.watch(KEY);

        // 开启事务
        redisTemplateObj.multi();
        // 减1
        opsForValue.decrement(KEY);
        // 业务操作
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 提交事务
        List<Object> exec = redisTemplateObj.exec();

        System.out.println(exec);

        if (exec == null) {
            logger.info("====>>>> 没的抢到...");
        } else {
            logger.info("====>>>> 抢到了...");
        }


    }


}
