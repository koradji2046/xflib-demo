/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis.command;

import org.springframework.boot.CommandLineRunner;

import com.xflib.framework.redis.JsonRedisTemplate;
import com.xflib.framework.redis.utils.DynamicRedisHolder;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class RedisSampleCommandLineRunner implements CommandLineRunner {

//	@Autowired
    private JsonRedisTemplate redis;
    
    public RedisSampleCommandLineRunner(){
    }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("=> 测试类, 将直接打印输出到屏幕:");

        redis=DynamicRedisHolder.getRedisTemplateBySite(JsonRedisTemplate.class,"default");
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));
    
        redis=DynamicRedisHolder.getRedisTemplate(JsonRedisTemplate.class,"30001","xk");
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));

        redis=DynamicRedisHolder.getRedisTemplateBySite(JsonRedisTemplate.class,"30002");
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));
    
        DynamicRedisHolder.removeContext();
        
        System.out.println("=> 上线前请清除这个测试类: "+this.getClass().getName());
    }

}
