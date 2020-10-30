package com.billow.springbootredis.api;

import com.billow.springbootredis.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuyongtao
 * @create 2020-09-15 14:54
 */
@RestController
public class TestApi {

    @Autowired
    private TestService testService;

    @GetMapping("/reset")
    public void reset() {
        testService.reset();
    }

    @GetMapping("/decrement")
    public void decrement() {
        String orderId = "0000";
        testService.decrement(orderId);
    }
}
