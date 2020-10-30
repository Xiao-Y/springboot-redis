package com.billow.springbootredis.custom;

import com.billow.springbootredis.annotation.CustomMapper;

/**
 * @author liuyongtao
 * @create 2020-10-30 14:47
 */
@CustomMapper
public class CustomService {

    public void reset() {
        System.out.println("reset");
    }
}
