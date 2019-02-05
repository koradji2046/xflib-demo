package com.xflib.demo.amqp;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import com.xflib.demo.configuration.amqp.DemoAdminConfiguration;
import com.xflib.framework.amqp.DynamicRabbitConnectionFactory;

public class RabbitReceiver {
    private static final Logger log = LoggerFactory.getLogger(RabbitSender.class);

    @Autowired
    private DynamicRabbitConnectionFactory dynamicRabbitConnectionFactory;

    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;
    
    private MessageListener listener = new MessageListener() {
       @Override
        public void onMessage(Message message) {
           RabbitMessageDataBean c= (RabbitMessageDataBean) jackson2JsonMessageConverter.fromMessage(message);
           log.info(c.getSite());
        }
    };

    @PostConstruct
    public void init() {
        dynamicRabbitConnectionFactory.RegisterMessageListener(
                DemoAdminConfiguration.dynamic_test_queue,
                container->{
                    container.setQueueNames(DemoAdminConfiguration.dynamic_test_queue);
                    container.setMessageListener(new MessageListenerAdapter(listener));
                },
                true);
    }

}