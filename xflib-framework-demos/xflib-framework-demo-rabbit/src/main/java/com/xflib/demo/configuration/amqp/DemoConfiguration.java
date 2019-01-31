/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.configuration.amqp;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xflib.demo.amqp.RabbitReceiver;
import com.xflib.demo.amqp.RabbitSender;
import com.xflib.framework.amqp.DynamicRabbitProperties;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
public class DemoConfiguration {

    public final static String dynamic_test_exchange = "dynamic.test.exchange";
    public final static String dynamic_test_queue = "dynamic.test.queue";
    public final static String dynamic_test_queue_routerKey = "dynamic.test.queue";

    @Autowired
    AmqpAdmin admin;

    @Autowired
    private DynamicRabbitProperties dynamicRabbitProperties;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void exchange() {
        // 创建Exchange
        String exchangeName = dynamic_test_exchange;
        TopicExchange exchange = new TopicExchange(exchangeName);

        // 创建Queue
        String queueName = dynamic_test_queue;
        Queue queue = new Queue(queueName, true, false, false, null);
        /*
         * 构造一个新的队列，给定名称、耐久性标志、自动删除标志和参数。参数： name - 名称队列的名称-不能为空；设置为“”以使代理生成该名称。
         * durable - 如果我们声明一个持久队列，则持久为真（该队列将在服务器重新启动后继续存在） exclusive -
         * 如果声明独占队列，则返回exclusive true（该队列将仅由声明者的连接使用） autoDelete -
         * 如果服务器不再使用队列时应将其删除，则自动删除为true arguments -参数用于声明队列的参数
         */
        dynamicRabbitProperties.getSites().forEach((site) -> {
            String beanName = String.format("rabbit-%s", site);
            SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), beanName);
            admin.declareExchange(exchange);
            admin.declareQueue(queue);
            admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(dynamic_test_queue_routerKey));
            SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
        });

    }

    @Bean
    @ConditionalOnProperty(prefix = "demo.enabled", name = "rabbitSender", havingValue = "true", matchIfMissing = false)
    public RabbitSender rabbitSender() {
        return new RabbitSender();
    }

    @Bean
    public RabbitReceiver rabbitReceiver() {
        return new RabbitReceiver();
    }

}
