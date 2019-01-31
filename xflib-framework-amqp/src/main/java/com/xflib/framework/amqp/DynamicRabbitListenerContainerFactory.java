package com.xflib.framework.amqp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class DynamicRabbitListenerContainerFactory {
    private Logger log = LoggerFactory.getLogger(DynamicRabbitConnectionFactory.class);

    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;
    @Autowired
    private DynamicRabbitConnectionFactory dynamicRabbitConnectionFactory;

    @PostConstruct
    public void init() {
        dynamicRabbitConnectionFactory.redisContexts.forEach((context) -> {
            createSimpleMessageListenerContainer(context);
        });
    }

    /**
     * 根据redisContext获取SimpleMessageListenerContainer
     * 
     * @param redisContext
     * @return
     */
    public SimpleMessageListenerContainer getContainer(String redisContext) {
        SimpleMessageListenerContainer result;
        if (containers.containsKey(redisContext)) {
            result = containers.get(redisContext);
        } else {
            result = createSimpleMessageListenerContainer(redisContext);
        }

        return result;
    }

    public void setListenner(Consumer<SimpleMessageListenerContainer> callback, 
            boolean autoStartUp) {
        dynamicRabbitConnectionFactory.redisContexts.forEach((context) -> {
            SimpleMessageListenerContainer container = containers.get(context);
            callback.accept(container);
            if (autoStartUp) {
                container.start();
                if (log.isDebugEnabled()) {
                    log.debug("=> Message Listener for queues-[{}] has Started.",
                            StringUtils.join(container.getQueueNames(), ","));
                }
            } else {
                log.debug("=> Message Listener for queues-[{}] has Registed.",
                        StringUtils.join(container.getQueueNames(), ","));
            }
        });
    }

    private SimpleMessageListenerContainer createSimpleMessageListenerContainer(
            String redisContext) {
        ConnectionFactory factory = dynamicRabbitConnectionFactory
                .getTargetConnectionFactory(redisContext);
        SimpleMessageListenerContainer simpleMessageListenerContainer =
                new SimpleMessageListenerContainer(factory);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(factory);
        simpleMessageListenerContainer.setRabbitAdmin(rabbitAdmin);
        simpleMessageListenerContainer.setMessageConverter(
                jackson2JsonMessageConverter);
        containers.put(redisContext, simpleMessageListenerContainer);
        return simpleMessageListenerContainer;
    }

    private final Map<Object, SimpleMessageListenerContainer> containers = 
            new ConcurrentHashMap<Object, SimpleMessageListenerContainer>();

}
