package com.billow.springbootredis;

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

    @Test
    public void test() {
        opsForValue.set(KEY, "iii");
        System.out.println(opsForValue.get(KEY)); // iii
    }

    @Test
    public void test1() {
        opsForValue.set(KEY, "ddd", 3, TimeUnit.SECONDS);
        System.out.println(opsForValue.get(KEY)); // ddd
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(opsForValue.get(KEY)); // null
    }

    @Test
    public void test2() {
        opsForValue.set(KEY, "iii", 8, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(redisTemplate.getExpire(KEY));// 过期后返回 -2
        }
    }

    @Test
    public void test3() {
        opsForValue.set(KEY, "0123456789");
        opsForValue.set(KEY, "TTT", 2);
        System.out.println(opsForValue.get(KEY)); // 01TTT56789
    }

    // 如果不存在就设置
    @Test
    public void test4() {
        opsForValue.set(KEY, "0123456789");

        System.out.println(opsForValue.setIfAbsent(KEY, "336")); // false
        System.out.println(opsForValue.get(KEY)); // 0123456789

        redisTemplate.delete(KEY); // true

        System.out.println(opsForValue.setIfAbsent(KEY, "TTT")); // true
        System.out.println(opsForValue.get(KEY)); // TTT
    }

    // 如果存在就设置
    @Test
    public void test10() {
        opsForValue.set(KEY, "0123456789");

        System.out.println(opsForValue.setIfPresent(KEY, "555")); // true
        System.out.println(opsForValue.get(KEY)); // 555

        redisTemplate.delete(KEY);

        System.out.println(opsForValue.setIfPresent(KEY, "999")); // false
        System.out.println(opsForValue.get(KEY)); // null
    }

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

        System.out.println(opsForValue.multiGet(strs)); // [a, b, x, c, t, null]
    }

    @Test
    public void test6() {
        ValueOperations<String, Object> opsForValue1 = redisTemplateObj.opsForValue();
        opsForValue1.set(KEY, 2);

        System.out.println(opsForValue1.increment(KEY)); // 3
        System.out.println(opsForValue1.increment(KEY, 7)); // 10

        System.out.println(opsForValue1.decrement(KEY)); // 9
        System.out.println(opsForValue1.decrement(KEY, 7)); // 2
    }

    @Test
    public void test7() {
        opsForValue.set(KEY, "0123456789");

        System.out.println(opsForValue.getAndSet(KEY, "PPP")); // 0123456789
        System.out.println(opsForValue.get(KEY)); // PPP
    }

    @Test
    public void test8() {
        opsForValue.set(KEY, "0123456789");

        System.out.println(opsForValue.append(KEY, "PPP")); // 13
        System.out.println(opsForValue.get(KEY)); // 0123456789PPP
    }

    @Test
    public void test9() {
        opsForValue.set(KEY, "0123456789");

        System.out.println(opsForValue.size(KEY)); // 10
        System.out.println(opsForValue.get(KEY, 2, 4)); // 234
    }
}
