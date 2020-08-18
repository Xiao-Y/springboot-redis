package com.billow.springbootredis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author liuyongtao
 * @create 2020-08-17 13:41
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class OpsForSortSetTests {

    public static final String KEY = "zset_key";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ZSetOperations<String, String> opsForZSet;

    @Before
    public void before() {
        opsForZSet = redisTemplate.opsForZSet();
        redisTemplate.delete(KEY);
    }

    // 添加元素（不可重复）
    @Test
    public void add() {
        Boolean add = opsForZSet.add(KEY, "1", 1.9);// 返回是否添加成功
        log.info("是否添加成功:{}", add);

        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
        set.add(new DefaultTypedTuple<>("2", 2.4));
        set.add(new DefaultTypedTuple<>("3", 2.1));
        set.add(new DefaultTypedTuple<>("7", 2.2));
        set.add(new DefaultTypedTuple<>("9", 2.8));
        set.add(new DefaultTypedTuple<>("8", 3.0));
        set.add(new DefaultTypedTuple<>("4", 3.1));
        Long aLong = opsForZSet.add(KEY, set); // 返回添加成功的个数

        log.info("成功添加{}个", aLong);
        Set<String> range = opsForZSet.range(KEY, 0, -1); // [1, 3, 7, 2, 9, 8, 4]
        log.info("所有元素：{}", range);
    }

    // 截取元素
    @Test
    public void range() {
        this.add();

        Set<String> range = opsForZSet.range(KEY, 0, -1); // [1, 3, 7, 2, 9, 8, 4]
        log.info("所有元素：{}", range);

        // 正序下标获取
        range = opsForZSet.range(KEY, 0, 3);// [1, 3, 7, 2]
        log.info("所有元素：{}", range);

        // 获取指定分数范围的
        range = opsForZSet.rangeByScore(KEY, 2, 3);// [3, 7, 2, 9, 8]
        log.info("所有元素：{}", range);

        // 原始 [1, 3, 7, 2, 9, 8, 4]，截取 元素 "9" 之前的
        range = opsForZSet.rangeByLex(KEY, RedisZSetCommands.Range.range().lt("9"));// [1, 3, 7, 2]
        log.info("所有元素：{}", range);
        // 截取 元素"3"~"9" 之间的
        range = opsForZSet.rangeByLex(KEY, RedisZSetCommands.Range.range().gt("3").lt("9"));// [7, 2]
        log.info("所有元素：{}", range);

        // 逆序下标获取
        range = opsForZSet.reverseRange(KEY, 0, 3); // [4, 8, 9, 2]
        log.info("所有元素：{}", range);
    }

    // 统计指定分数范围的个数
    @Test
    public void count() {
        this.add();

        Long count = opsForZSet.count(KEY, 2.1, 3);
        log.info("得分为min~max之间（包含）的个数：{}", count);// 5
    }

    // 获取指定元素的分数
    @Test
    public void score() {
        this.add();
        Double score = opsForZSet.score(KEY, "9");
        log.info("分数：{}", score);
    }

    // 对指定元素增加分数
    @Test
    public void incrementScore() {
        this.add();
        // 对指定元素增加分数
        Double aDouble = opsForZSet.incrementScore(KEY, "9", 5.6);
        log.info("分数：{}", aDouble);
    }

    // 交集，保存到指定key中
    @Test
    public void intersectAndStore() {
        this.add();

        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
        set.add(new DefaultTypedTuple<>("2", 2.4));
        set.add(new DefaultTypedTuple<>("3", 2.1));
        set.add(new DefaultTypedTuple<>("7", 2.2));
        set.add(new DefaultTypedTuple<>("9", 2.8));
        set.add(new DefaultTypedTuple<>("8", 3.0));
        set.add(new DefaultTypedTuple<>("4", 3.1));

        String key2 = KEY + 2;
        opsForZSet.add(key2, set); // 返回添加成功的个数

        Set<String> range = opsForZSet.range(key2, 0, -1); // [3, 7, 2, 9, 8, 4]
        log.info("所有元素2：{}", range);

        // 交集
        String key3 = KEY + 3;
        Long aLong = opsForZSet.intersectAndStore(KEY, key2, key3);// 6
        log.info("aLong：{}", aLong);
        range = opsForZSet.range(key3, 0, -1); // [3, 7, 2, 9, 8, 4]
        log.info("交集：{}", range);

        // key2与一个集合的交集
        String key4 = KEY + 4;
        aLong = opsForZSet.intersectAndStore(key2, Collections.singleton(key2), key4);// 6
        log.info("aLong：{}", aLong);
        range = opsForZSet.range(key4, 0, -1); // [3, 7, 2, 9, 8, 4]
        log.info("交集：{}", range);
    }

    // 并集
    @Test
    public void union() {
        this.add();

        Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
        set.add(new DefaultTypedTuple<>("2", 2.4));
        set.add(new DefaultTypedTuple<>("3", 2.5));
        set.add(new DefaultTypedTuple<>("7", 2.8));
        set.add(new DefaultTypedTuple<>("19", 2.8));
        set.add(new DefaultTypedTuple<>("8", 3.4));
        set.add(new DefaultTypedTuple<>("14", 3.1));

        String key2 = KEY + 2;
        opsForZSet.add(key2, set); // 返回添加成功的个数

        Set<String> range = opsForZSet.range(key2, 0, -1); // [2, 3, 19, 7, 9, 14, 4, 8]
        log.info("所有元素2：{}", range);

        String key3 = KEY + 3;
        opsForZSet.unionAndStore(KEY, key2, key3);//

        range = opsForZSet.range(key3, 0, -1); // [1, 19, 14, 3, 2, 7, 9, 4, 8]
        log.info("并集：{}", range);
    }

    // 获取指定元素的下标
    @Test
    public void rank() {
        this.add(); //[1, 3, 7, 2, 9, 8, 4]

        Long rank = opsForZSet.rank(KEY, "7"); // 2
        log.info("rank:{}", rank);

        rank = opsForZSet.reverseRank(KEY, "7"); // 4
        log.info("rank:{}", rank);
    }

    // 删除元素
    @Test
    public void remove() {
        this.add();

        // 删除指定的元素
        Long remove = opsForZSet.remove(KEY, "9", "2");// 2
        log.info("remove的个数:{}", remove);
        Set<String> range = opsForZSet.range(KEY, 0, -1); // [1, 3, 7, 8, 4]
        log.info("所有元素2：{}", range);

        // 删除指定下标的元素
        remove = opsForZSet.removeRange(KEY, 1, 2);// 2
        log.info("remove的个数:{}", remove);
        range = opsForZSet.range(KEY, 0, -1); // [1, 8, 4]
        log.info("所有元素2：{}", range);

        // 删除指定分数范围的元素
        remove = opsForZSet.removeRangeByScore(KEY, 3, 4);// 2
        log.info("remove的个数:{}", remove);
        range = opsForZSet.range(KEY, 0, -1); // [1]
        log.info("所有元素2：{}", range);
    }

    @Test
    public void zCard() {
        this.add();

        Long aLong = opsForZSet.zCard(KEY);
        log.info("aLong：{}", aLong);

        Long size = opsForZSet.size(KEY);
        log.info("size：{}", size);


    }
}
