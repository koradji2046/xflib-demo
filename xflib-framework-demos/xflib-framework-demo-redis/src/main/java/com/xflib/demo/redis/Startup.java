/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author koradji
 * @date 2019/1/27
 */
@SpringBootApplication
@ComponentScan({
    "com.xflib.*.configuration",
    })
@EnableAutoConfiguration(exclude = {
 		RedisAutoConfiguration.class,
 })
public class Startup {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Startup.class, args);
    }
}
