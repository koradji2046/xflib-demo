package com.xflib.demo.amqp;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

import com.xflib.framework.amqp.AbstractReceiver;

public class Receiver extends AbstractReceiver {
    @PostConstruct
    public void init() {
        dynamicRabbitConnectionFactory.RegisterMessageListener(
                Constants.dynamic_test_queue,
                (container, beanName) -> {
                    MessageListenerExt<DataBean> listener = new MessageListenerExt<DataBean>() {

                        @Override
                        protected void beforProcess(MessageProperties messageProperties) {}

                        @Override
                        protected void process(DataBean message) {
                          log.info("=>Recived data from {}: {}", this.getWho(), message.toString());
                        }
//                        @Override
//                        public void onMessage(Message message) {
//                            message.getMessageProperties().getMessageCount();
//                            DataBean c = (DataBean) jackson2JsonMessageConverter
//                                    .fromMessage(message);
//                            log.info("=>Recived data from {}: {}", this.getWho(), c.toString());
//                        }
                    };
                    container.setQueueNames(Constants.dynamic_test_queue);
                    listener.setWho(beanName);
                    container.setMessageListener(new MessageListenerAdapter(listener));
                }, true);
    }

}