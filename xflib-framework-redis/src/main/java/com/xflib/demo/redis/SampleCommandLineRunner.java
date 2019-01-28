/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.xflib.framework.redis.DynamicRedisHolder;

/**
 * @author koradji
 * @date 2019/1/27
 */
@Component
@ConditionalOnProperty(prefix="demo.enabled",name="sampleCommandLineRunner",havingValue="true",matchIfMissing=false)
public class SampleCommandLineRunner implements CommandLineRunner {

    private RedisTemplate<String, Object> redis;
    
    public SampleCommandLineRunner(){
    }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("=> 测试类, 将直接打印输出到屏幕:");

        redis=DynamicRedisHolder.getRedisTemplateBySite("default");
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));
    
        redis=DynamicRedisHolder.getRedisTemplate("30001","xk");
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));

        redis=DynamicRedisHolder.getRedisTemplateBySite("30002");
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));
    
        System.out.println("=> 上线前请清除这个测试类: "+this.getClass().getName());
    }

}
