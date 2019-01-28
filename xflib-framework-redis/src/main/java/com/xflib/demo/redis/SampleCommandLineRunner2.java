/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.xflib.framework.redis.DynamicRedisHolder;

/**
 * @author koradji
 * @date 2019/1/27
 */
@Component//@Order(2)
@ConditionalOnProperty(prefix="demo.enabled",name="sampleCommandLineRunner2",havingValue="true",matchIfMissing=false)
public class SampleCommandLineRunner2 implements CommandLineRunner {

    @Autowired
    private SampleCommandLineRunner2Service sampleCommandLineRunner2Service;
    
    public SampleCommandLineRunner2(){
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=> 测试类, 将直接打印输出到屏幕:");

        DynamicRedisHolder.setSite("default");
        sampleCommandLineRunner2Service.run();
        DynamicRedisHolder.setSite("30001");
        sampleCommandLineRunner2Service.run();
        DynamicRedisHolder.setSite("30002");
        sampleCommandLineRunner2Service.run();
        
        System.out.println("=> 上线前请清除这个测试类: "+this.getClass().getName());
    }

}
