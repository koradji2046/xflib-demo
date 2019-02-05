/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration.amqp;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xflib.demo.amqp.Constants;
import com.xflib.demo.amqp.Receiver;
import com.xflib.demo.amqp.Sender;
import com.xflib.framework.configuration.amqp.AbstractAmqpAdminConfiguration;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitAdmin", havingValue = "true", matchIfMissing = false)
public class DemoAdminConfiguration extends AbstractAmqpAdminConfiguration{

    @PostConstruct
    public void exchange() {
        this.configs.add(new Config(
                Constants.dynamic_test_exchange, 
                Constants.dynamic_test_queue, 
                Constants.dynamic_test_queue_routingKey,
                Constants.dynamic_test_queue_routingKeyA));
        this.init();
    }

    @Bean
    @ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitSender", havingValue = "true", matchIfMissing = false)
    public Sender rabbitSender() {
        return new Sender();
    }

    @Bean
    @ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitReciver", havingValue = "true", matchIfMissing = false)
    public Receiver rabbitReceiver() {
        return new Receiver();
    }
}
