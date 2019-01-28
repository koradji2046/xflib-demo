/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.xflib.framework.utils.SpringUtils;

/**
 * @author koradji
 * @date 2019/1/27
 */
@Component
@ConditionalOnProperty(prefix="demo.enabled",name="sampleCommandLineRunner2",havingValue="true",matchIfMissing=false)
public class SampleCommandLineRunner2Service {

//    @Autowired
    private RedisTemplate<String, Object> redis;
    
    public SampleCommandLineRunner2Service(){
    }

    @SuppressWarnings("unchecked")
    public void run() {
        
        redis=SpringUtils.getBean("redisTemplate",RedisTemplate.class);
        redis.opsForValue().set("a", 1);
        redis.opsForValue().set("b", "abc");
        System.out.println(redis.opsForValue().get("a"));
        System.out.println(redis.opsForValue().get("b"));
    }

}
