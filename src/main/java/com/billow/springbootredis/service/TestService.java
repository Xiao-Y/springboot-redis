package com.billow.springbootredis.service;

public interface TestService {
    void decrement(String orderId);

    void reset();

}
