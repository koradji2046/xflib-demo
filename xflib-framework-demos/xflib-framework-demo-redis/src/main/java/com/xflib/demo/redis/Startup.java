/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.xflib.framework.redis.annotation.EnabledDynamicRedis;

/**
 * @author koradji
 * @date 2019/1/27
 */
@SpringBootApplication
@ComponentScan({
    "com.xflib.*.autoconfigure",
    })
//@EnableAutoConfiguration(exclude = {
// 		RedisAutoConfiguration.class,
// })
@EnabledDynamicRedis
public class Startup {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Startup.class, args);
    }
}
