package com.billow.springbootredis.service.impl;

import com.billow.springbootredis.service.TestService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyongtao
 * @create 2020-09-15 14:57
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private RedissonClient redissonClient;

    public int a = 100;

    Lock lock = new ReentrantLock();

    @Override
    public void decrement(String orderId) {
        lock.lock();
        try {
            if (a > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a--;
            }
            System.out.println(a);
        } finally {
            lock.unlock();
        }

        // 分布式锁
//        RLock lock = redissonClient.getLock(orderId);
//        try {
//            lock.lock();
//            if (a > 0) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                a--;
//            }
//            System.out.println(a);
//        } finally {
//            lock.unlock();
//        }
    }

    @Override
    public void reset() {
        a = 100;
    }
}
