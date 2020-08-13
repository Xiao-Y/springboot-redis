package com.billow.springbootredis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class OpsForValueTests {

    public static final String KEY = "string_key";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplateObj;

    ValueOperations<String, String> opsForValue = null;

    @Before
    public void before() {
        opsForValue = redisTemplate.opsForValue();
    }

    // 保存值
    @Test
    public void test() {
        opsForValue.set(KEY, "iii");
        log.info("===>>>" + opsForValue.get(KEY)); // iii
    }

    // 保存含有过期时间的
    @Test
    public void test1() {
        opsForValue.set(KEY, "ddd", 3, TimeUnit.SECONDS);
        log.info("===>>>" + opsForValue.get(KEY)); // ddd
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("===>>>" + opsForValue.get(KEY)); // null
    }

    // 查看过期时间
    @Test
    public void test2() {
        opsForValue.set(KEY, "iii", 8, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("===>>>" + redisTemplate.getExpire(KEY));// 过期后返回 -2
        }
    }

    // 从指定位置覆盖，下标从 0 开始
    @Test
    public void test3() {
        opsForValue.set(KEY, "0123456789");
        opsForValue.set(KEY, "TTT", 2);
        log.info("===>>>" + opsForValue.get(KEY)); // 01TTT56789
    }

    // 如果不存在就设置
    @Test
    public void test4() {
        opsForValue.set(KEY, "0123456789");

        log.info("===>>>" + opsForValue.setIfAbsent(KEY, "336")); // false
        log.info("===>>>" + opsForValue.get(KEY)); // 0123456789

        redisTemplate.delete(KEY); // true

        log.info("===>>>" + opsForValue.setIfAbsent(KEY, "TTT")); // true
        log.info("===>>>" + opsForValue.get(KEY)); // TTT
    }

    // 如果存在就设置
    @Test
    public void test10() {
        opsForValue.set(KEY, "0123456789");

        log.info("===>>>" + opsForValue.setIfPresent(KEY, "555")); // true
        log.info("===>>>" + opsForValue.get(KEY)); // 555

        redisTemplate.delete(KEY);

        log.info("===>>>" + opsForValue.setIfPresent(KEY, "999")); // false
        log.info("===>>>" + opsForValue.get(KEY)); // null
    }

    // 一次设置多个，一次获取多个
    @Test
    public void test5() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "x");
        map.put("4", "c");
        map.put("5", "t");
        opsForValue.multiSet(map);

        List<String> strs = Arrays.asList("1", "2", "3", "4", "5", "6");

        log.info("===>>>" + opsForValue.multiGet(strs)); // [a, b, x, c, t, null]
    }

    // 对数据累加或者递减
    @Test
    public void test6() {
        ValueOperations<String, Object> opsForValue1 = redisTemplateObj.opsForValue();
        opsForValue1.set(KEY, 2);

        log.info("===>>>" + opsForValue1.increment(KEY)); // 3
        log.info("===>>>" + opsForValue1.increment(KEY, 7)); // 10

        log.info("===>>>" + opsForValue1.decrement(KEY)); // 9
        log.info("===>>>" + opsForValue1.decrement(KEY, 7)); // 2
    }

    // 获取旧值，设置新值
    @Test
    public void test7() {
        opsForValue.set(KEY, "0123456789");

        log.info("===>>>" + opsForValue.getAndSet(KEY, "PPP")); // 0123456789
        log.info("===>>>" + opsForValue.get(KEY)); // PPP
    }

    // 字符串追加，返回长度
    @Test
    public void test8() {
        opsForValue.set(KEY, "0123456789");

        log.info("===>>>" + opsForValue.append(KEY, "PPP")); // 13
        log.info("===>>>" + opsForValue.get(KEY)); // 0123456789PPP
    }

    // 截取字符串
    @Test
    public void test9() {
        opsForValue.set(KEY, "0123456789");

        log.info("===>>>" + opsForValue.size(KEY)); // 10
        log.info("===>>>" + opsForValue.get(KEY, 2, 4)); // 234
    }
}
