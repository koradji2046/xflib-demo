/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.xflib.demo.redis.SampleCommandLineRunner;
import com.xflib.demo.redis.SampleCommandLineRunner2;
import com.xflib.framework.utils.SpringUtils;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
public class DemoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix="demo.enabled",name="sampleCommandLineRunner",havingValue="true",matchIfMissing=false)
    public SampleCommandLineRunner sampleCommandLineRunner() {
        return new SampleCommandLineRunner();
    }

    @Bean
    @ConditionalOnProperty(prefix="demo.enabled",name="sampleCommandLineRunner2",havingValue="true",matchIfMissing=false)
    public SampleCommandLineRunner2 sampleCommandLineRunner2() {
        return new SampleCommandLineRunner2();
    }
}
