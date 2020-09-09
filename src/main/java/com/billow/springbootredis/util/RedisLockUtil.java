package com.billow.springbootredis.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * Created by IDEA
 * User: shma1664
 * Date: 2016-08-16 14:01
 * Desc: redis分布式锁
 */
public final class RedisLockUtil {

    private static final int defaultExpire = 60;

    private RedisLockUtil() {
    }

    /**
     * 加锁
     *
     * @param key    redis key
     * @param expire 过期时间，单位秒
     * @return true:加锁成功，false，加锁失败
     */
    public static boolean lock(String key, int expire) {
        RedisTemplate<String, String> redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);
        ValueOperations<String, String> redisService = redisTemplate.opsForValue();

        Boolean status = redisService.setIfAbsent(key, "1");

        if (status) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }

    public static boolean lock(String key) {
        return lock2(key, defaultExpire);
    }

    /**
     * 加锁
     *
     * @param key    redis key
     * @param expire 过期时间，单位秒
     * @return true:加锁成功，false，加锁失败
     */
    public static boolean lock2(String key, long expire) {
        RedisTemplate<String, String> redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);
        ValueOperations<String, String> redisService = redisTemplate.opsForValue();

        long value = System.currentTimeMillis() + expire;
        Boolean status = redisService.setIfAbsent(key, String.valueOf(value));

        if (status) {
            return true;
        }
        long oldExpireTime = Long.parseLong(redisService.get(key));
        if (oldExpireTime < System.currentTimeMillis()) {
            //超时
            long newExpireTime = System.currentTimeMillis() + expire;
            long currentExpireTime = Long.parseLong(redisService.getAndSet(key, String.valueOf(newExpireTime)));
            if (currentExpireTime == oldExpireTime) {
                return true;
            }
        }
        return false;
    }

    public static void unLock1(String key) {
        RedisTemplate<String, String> redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);

        redisTemplate.delete(key);
    }

    public static void unLock2(String key) {
        RedisTemplate<String, String> redisTemplate = SpringContextUtil.getBean(RedisTemplate.class);
        ValueOperations<String, String> redisService = redisTemplate.opsForValue();

        long oldExpireTime = Long.parseLong(redisService.get(key));
        if (oldExpireTime > System.currentTimeMillis()) {
            redisTemplate.delete(key);
        }
    }

}