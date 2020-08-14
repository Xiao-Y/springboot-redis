package com.billow.springbootredis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * set 操作测试
 *
 * @author liuyongtao
 * @create 2020-08-14 10:29
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisApplication.class)
public class OpsForSetTests {

    public static final String KEY = "set_key";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    SetOperations<String, String> opsForSet;

    @Before
    public void before() {
        opsForSet = redisTemplate.opsForSet();
        redisTemplate.delete(KEY);
    }

    // 添加，添加进去的顺序与出来的顺序不一定相同,元素不可以重复,返回添加的元素个数
    @Test
    public void add() {
        Long add = opsForSet.add(KEY, "1", "A", "S", "5", "A", "5", "A", "4", "5"); // 5
        log.info("====>>>" + add);
        // 查询出所有元素
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);

        members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);
    }

    // 移除指定元素
    @Test
    public void remove() {
        Long add = opsForSet.add(KEY, "1", "A", "S", "5", "A", "5", "A", "4", "5"); // 5
        log.info("====>>>" + add);
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);
        opsForSet.remove(KEY, "A");
        members = opsForSet.members(KEY);// [1, 5, S, 4]
        log.info("====>>>" + members);
    }

    // 判断某个元素是否存在
    @Test
    public void isMember() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // 5
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);

        Boolean member = opsForSet.isMember(KEY, "A"); // true
        log.info("====>>>" + member);
    }

    // 移动元素到另外一个set中,如果原set中不存在,则返回false
    @Test
    public void move() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // 5
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);

        Boolean move = opsForSet.move(KEY, "5", "set_key2"); // true
        log.info("====>>>" + move);

        move = opsForSet.move(KEY, "99", "set_key2"); // false
        log.info("====>>>" + move);

        members = opsForSet.members(KEY);// [4, A, 1, S]
        log.info("====>>>" + members);

        opsForSet.add(KEY, "5"); // 5
        move = opsForSet.move(KEY, "5", "set_key2"); // true
        log.info("====>>>" + move);

        members = opsForSet.members("set_key2");// [5]
        log.info("====>>>" + members);

    }

    // 随机弹出指定个数元素，3.2版本
    @Test
    public void pop() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // [1, 5, S , A, 4]
        List<String> pop = opsForSet.pop(KEY, 3);// [1, A, 4]
        log.info("====>>>" + pop);

        Set<String> members = opsForSet.members(KEY);// [5, S]
        log.info("====>>>" + members);
    }

    // 随机获取 指定个数元素,(可以重复获取)
    @Test
    public void randomMembers() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // 5
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);

        String member = opsForSet.randomMember(KEY);// "5"
        log.info("====>>>" + member);


        List<String> list = opsForSet.randomMembers(KEY, 10);// [S, 4, 5, S, A, S, 1, 1, S, A]
        log.info("====>>>" + list);

        members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);
    }

    // 随机获取 指定个数元素(不可以重复)
    @Test
    public void distinctRandomMembers() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // 5
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);

        Set<String> set = opsForSet.distinctRandomMembers(KEY, 30);// [1, S, 4, A, 5]
        log.info("====>>>" + set);

        members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>" + members);
    }

    // 并集
    @Test
    public void union() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // 5
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>KEY " + members);

        String key2 = KEY + "2";
        opsForSet.add(key2, "2", "S", "Q", "4", "5"); // [5, S, 2, Q, 4]
        members = opsForSet.members(key2);// [1, 5, S, A, 4]
        log.info("====>>>key2 " + members);

        // 指定两个key,求并集
        Set<String> union = opsForSet.union(key2, KEY); // [Q, 1, 5, S, 2, A, 4]
        log.info("====>>>union " + union);

        // 指定多个 key ,求并集
        List<String> keys = Arrays.asList(key2, KEY);
        union = opsForSet.union(keys);
        log.info("====>>>union " + union);

        // 指定两个key,求并集,保存到指定key中
        String key3 = KEY + "2";
        Long aLong = opsForSet.unionAndStore(key2, KEY, key3); // 7
        log.info("====>>>aLong " + aLong);
        members = opsForSet.members(key3);//  [Q, 1, 5, 2, S, 4, A]
        log.info("====>>>key3 " + members);

        // 指定多个 key ,求并集 ,保存到指定key中
        opsForSet.unionAndStore(keys, key3);
        members = opsForSet.members(key3);//  [Q, 1, 5, 2, S, A, 4]
        log.info("====>>>key3 " + members);

        // 指定多个 key ,求并集,保存到指定key中
        opsForSet.unionAndStore(KEY, keys, key3);
        members = opsForSet.members(key3);//  [Q, 1, 5, 2, S, 4, A]
        log.info("====>>>key3 " + members);
    }

    @Test
    public void differ() {
        opsForSet.add(KEY, "1", "S", "A", "4", "5"); // 5
        Set<String> members = opsForSet.members(KEY);// [1, 5, S, A, 4]
        log.info("====>>>KEY " + members);

        String key2 = KEY + "2";
        redisTemplate.delete(key2);
        opsForSet.add(key2, "1", "2", "S", "Q", "4", "5"); // 6
        members = opsForSet.members(key2);// [S, 2, Q, 1, 5, 4]
        log.info("====>>>key2 " + members);

        // key2 的差集( key2 中存在但是在 KEY 中不存在的)
        Set<String> difference = opsForSet.difference(key2, KEY);// [Q, 2]
        log.info("====>>>key2 " + difference);

        // KEY 的差集( KEY 中存在但是在 key2 中不存在的)
        difference = opsForSet.difference(KEY, key2);// [A]
        log.info("====>>>key2 " + difference);


    }
}
