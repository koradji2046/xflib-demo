package com.xflib.demo.amqp;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.xflib.demo.configuration.amqp.DemoConfiguration;
import com.xflib.framework.amqp.DynamicRabbitListenerContainerFactory;

@Import(DynamicRabbitListenerContainerFactory.class)
public class RabbitReceiver {

    @Autowired
    private DynamicRabbitListenerContainerFactory listenerContainerFactory;

    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;
    
    private MessageListener listener = new MessageListener() {

       @Override
        public void onMessage(Message message) {
           RabbitMessageDataBean c= (RabbitMessageDataBean) jackson2JsonMessageConverter.fromMessage(message);
        }
    };

    @PostConstruct
    public void init() {
        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        listenerContainerFactory.setListenner(container -> {
            container.setQueueNames(DemoConfiguration.dynamic_test_queue);
            container.setMessageListener(adapter);
        }, true);
    }

}