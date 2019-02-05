/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration.amqp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xflib.demo.amqp.RabbitReceiver;
import com.xflib.demo.amqp.RabbitSender;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
public class DemoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitSender", havingValue = "true", matchIfMissing = false)
    public RabbitSender rabbitSender() {
        return new RabbitSender();
    }

    @Bean
    @ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitReciver", havingValue = "true", matchIfMissing = false)
    public RabbitReceiver rabbitReceiver() {
        return new RabbitReceiver();
    }

}
