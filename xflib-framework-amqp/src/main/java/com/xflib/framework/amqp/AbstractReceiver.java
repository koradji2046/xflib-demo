package com.xflib.framework.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractReceiver {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected DynamicRabbitConnectionFactory dynamicRabbitConnectionFactory;

    @Autowired
    protected Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    /**
     * MessageListener自定义扩展类
     * @author koradji
     *
     */
    public abstract class MessageListenerExt<T> implements MessageListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onMessage(Message message){
            this.beforProcess(message.getMessageProperties());
            T c = (T) jackson2JsonMessageConverter.fromMessage(message);
            if(log.isDebugEnabled()){
                log.debug("=>Recived data from {}: {}", this.getWho(), c.toString());
            }
            this.process(c);
        }

        protected abstract void beforProcess(MessageProperties messageProperties );
        protected abstract void process(T message);
        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }

        private String who;
    }

}