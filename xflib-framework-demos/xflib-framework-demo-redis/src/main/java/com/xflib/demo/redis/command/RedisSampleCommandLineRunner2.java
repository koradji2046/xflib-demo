/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.xflib.framework.redis.JsonRedisTemplate;
import com.xflib.framework.redis.utils.DynamicRedisHolder;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class RedisSampleCommandLineRunner2 implements CommandLineRunner {

    @Autowired
    private JsonRedisTemplate redis;

    public RedisSampleCommandLineRunner2() {
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=> 测试类, 将直接打印输出到屏幕:");

        _run("default","default");
        _run("30001","xk");
        _run("30002","xk");
        _run("30003","default");

        System.out.println("=> 上线前请清除这个测试类: " + this.getClass().getName());
    }

    private void _run(String site, String source) {
        DynamicRedisHolder.setContext(site, source);
        
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));
        
        DynamicRedisHolder.removeContext();
    }

}
