package com.billow.springbootredis;


import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class OpsForListTests {

    public static final String KEY = "list_key";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ListOperations<String, String> opsForList;

    @Before
    public void before() {
        opsForList = redisTemplate.opsForList();
    }

    // 从左边加入，返回队列长度
    @Test
    public void pushList() {
        Long aLong = opsForList.leftPush(KEY, "1");
        aLong = opsForList.leftPush(KEY, "2");
        aLong = opsForList.leftPush(KEY, "3");
        aLong = opsForList.leftPush(KEY, "4");
        log.info("===>>" + aLong); // 4
    }

    // 从右边获取值，并移出队列
    @Test
    public void popList() {
        while (true) {
            String pop = opsForList.rightPop(KEY);
            log.info("===>>" + pop);
        }
    }

    // 从右边弹出，并从左边插入。返回弹出值
    @Test
    public void rightPopAndLeftPush() {
        redisTemplate.delete(KEY);
        opsForList.leftPushAll(KEY, "1", "2", "3", "4", "5");

        String push = opsForList.rightPopAndLeftPush(KEY, KEY); // 1
        log.info("===>>" + push);
    }

    // 获取指定下标的元素，下标从 0 开始
    @Test
    public void index() {
        redisTemplate.delete(KEY);
        opsForList.leftPushAll(KEY, "1", "2", "3", "4", "5");

        String index = opsForList.index(KEY, 2);// 3
        log.info("===>>" + index);
    }

    // 从左边开始，截取指定位置数据
    @Test
    public void range() {
        redisTemplate.delete(KEY);
        opsForList.leftPushAll(KEY, "1", "2", "3", "4", "5");
        // 5/4/3/2/1
        List<String> range = opsForList.range(KEY, 2, 3);// [3, 2]
        log.info("===>>" + range);
        // end 为 -1时，表示截取到最后
        range = opsForList.range(KEY, 0, -1); //[5, 4, 3, 2, 1]
        log.info("===>>" + range);
    }

    // 从左边开始 移除3个字符串 "4",返回实际移除的数量
    @Test
    public void remove() {
        redisTemplate.delete(KEY);
        opsForList.leftPushAll(KEY, "1", "4", "2", "4", "3", "4", "4", "5", "4");

        List<String> range = opsForList.range(KEY, 0, -1); // [4, 5, 4, 4, 3, 4, 2, 4, 1]
        log.info("===>>" + range);
        // count 为 0时，移除所有
        Long remove = opsForList.remove(KEY, 3, "4"); // 3
        log.info("===>>" + remove);

        range = opsForList.range(KEY, 0, -1); //[5, 3, 4, 2, 4, 1]
        log.info("===>>" + range);
    }

}
