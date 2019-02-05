/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration.amqp;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.xflib.framework.configuration.amqp.AbstractAmqpAdminConfiguration;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitAdmin", havingValue = "true", matchIfMissing = false)
public class DemoAdminConfiguration extends AbstractAmqpAdminConfiguration{

    public final static String dynamic_test_exchange = "dynamic.test.exchange";
    public final static String dynamic_test_queue = "dynamic.test.queue";
    public final static String dynamic_test_queue_routingKey = "*";

    public final static String dynamic_test_queue_routingKeyA = "*.a";

    @PostConstruct
    public void exchange() {
        this.configs.add(new Config(dynamic_test_exchange, dynamic_test_queue, dynamic_test_queue_routingKey,dynamic_test_queue_routingKeyA));
        this.init();
    }

}
