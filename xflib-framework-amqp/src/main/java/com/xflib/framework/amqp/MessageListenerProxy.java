/** Copyright: Copyright (c) 2019, XFLIB studio */
package com.xflib.framework.amqp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author koradji
 * @date 2019/1/30
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MessageListenerProxy implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(MessageListenerProxy.class);

    @Autowired
    protected Jackson2JsonMessageConverter jackson2JsonMessageConverter;
    
    /**
     * 消息处理方法
     * @param messageContent
     * @return
     */
    protected abstract <T> Object handleMessage(T messageContent);
    
    /**
     * 路由方法
     * @param messageProperties
     * @return
     */
    protected abstract String beforeHandleMessage(MessageProperties messageProperties);

    private Map<String,MessageListener> messageListeners=new HashMap<>();
	@Override
    public void onMessage(Message message) {
        try {
            MessageProperties msssageProperties = message.getMessageProperties();
            String clazz=beforeHandleMessage(msssageProperties);
            if(!(clazz==null || clazz.isEmpty())&&messageListeners.containsKey(clazz)){
                MessageListener messageListener=messageListeners.get(clazz);
                messageListener.onMessage(message);
                if(logger.isDebugEnabled()){
                    logger.debug("Handle message by {}: {}", messageListener.getClass().getName(),clazz);
                }
           }else{
                if(logger.isWarnEnabled()){
                    logger.error("Not found Listenner to process message.");
                }
            }
        } catch (Throwable e) {
            logger.error("Handle message error, message is [ "+message+" ]", e);
        }
    }
	
	protected void regist(String id, MessageListener messageListener){
	    this.messageListeners.put(id, messageListener);
	}

}