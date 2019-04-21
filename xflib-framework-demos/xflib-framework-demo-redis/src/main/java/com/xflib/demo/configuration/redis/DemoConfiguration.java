/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xflib.demo.redis.command.RedisSampleCommandLineRunner;
import com.xflib.demo.redis.command.RedisSampleCommandLineRunner2;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
public class DemoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix="demo.enabled",name="redisSampleCommandLineRunner",havingValue="true",matchIfMissing=false)
    public RedisSampleCommandLineRunner sampleCommandLineRunner() {
        return new RedisSampleCommandLineRunner();
    }

    @Bean
    @ConditionalOnProperty(prefix="demo.enabled",name="redisSampleCommandLineRunner2",havingValue="true",matchIfMissing=false)
    public RedisSampleCommandLineRunner2 sampleCommandLineRunner2() {
        return new RedisSampleCommandLineRunner2();
    }
}
