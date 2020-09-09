package com.billow.springbootredis.redislock;

import com.billow.springbootredis.util.RedisLockUtil;

/**
 * @author liuyongtao
 * @create 2020-09-09 14:07
 */
public class TestRedisLock {
    public void drawRedPacket(long userId) {
        String key = "draw.redpacket.userid:" + userId;

        boolean lock = RedisLockUtil.lock2(key, 60);
        if (lock) {
            try {
                //领取操作
            } finally {
                //释放锁
                RedisLockUtil.unLock(key);
            }
        } else {
            new RuntimeException("重复领取奖励");
        }
    }
}
