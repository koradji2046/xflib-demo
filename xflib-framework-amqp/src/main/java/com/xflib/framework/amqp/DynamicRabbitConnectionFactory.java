/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.amqp;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRabbitConnectionFactory extends AbstractRoutingConnectionFactory{
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;
    
    public <T> void send( String exchange, String routingKey, String vHost, T payload) {

        Message data= jackson2JsonMessageConverter.toMessage(payload, new MessageProperties());
        data.getMessageProperties().setType(payload.getClass().getName());
        data.getMessageProperties().setHeader("__TypeId__", payload.getClass().getName());
        SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), vHost);
        rabbitTemplate.convertAndSend(exchange,routingKey,data);
        SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
    }

    private Logger log = LoggerFactory.getLogger(DynamicRabbitConnectionFactory.class);

    @Autowired
    private DynamicRabbitProperties dynamicRabbitProperties;

    @Autowired
    private RabbitProperties defaultRabbitProperties;

    // rabbitContext list
    List<String> rabbitSites=new ArrayList<>();
    
    @PostConstruct
    private void createRabbitConnectionFactory() throws UnknownHostException {
        
        dynamicRabbitProperties.getList().forEach((siteRabbitProperties)->{//创建站点指定rabbit数据源
          String site=siteRabbitProperties.getSite();
          RabbitProperties config=siteRabbitProperties.getConfig();
          String beanName=String.format("rabbit-%s", site);
          createRabbitConnectionFactory(beanName,config);
        });
        dynamicRabbitProperties.getSites().forEach((site)->{//检查并创建站点默认rabbit数据源
            String beanName=String.format("rabbit-%s", site);
            ConnectionFactory  connectionFactory =this.getTargetConnectionFactory(beanName);
            if (null==connectionFactory){
                createRabbitConnectionFactory(beanName,defaultRabbitProperties);
            }
        });
        String beanName=String.format("rabbit-%s", "default");
        ConnectionFactory  connectionFactory =this.getTargetConnectionFactory(beanName);
        if (null==this.getTargetConnectionFactory(beanName)){//检查并创建默认站点rabbit数据源
            connectionFactory=createRabbitConnectionFactory(beanName,defaultRabbitProperties);
        }
        
        // 设置默认Rabbit数据源
        this.setDefaultTargetConnectionFactory(connectionFactory);
    }

    // 监听器列表
    private Map<String,SimpleMessageListenerContainer> MessageListenerContainers=new ConcurrentHashMap<>();

    /**
     * 注册监听器
     * @param ListenerContainerId
     * @param factory
     * @param callback
     * @return
     */
    public void RegisterMessageListener( String ListenerContainerId, 
            Consumer<SimpleMessageListenerContainer> callback, boolean autoStartUp) {
        rabbitSites.forEach((key)->{
            ConnectionFactory factory=this.getTargetConnectionFactory(key);
            SimpleMessageListenerContainer simpleMessageListenerContainer =
                    new SimpleMessageListenerContainer(factory);
//            RabbitAdmin rabbitAdmin = new RabbitAdmin(factory);
//            simpleMessageListenerContainer.setRabbitAdmin(rabbitAdmin);
            simpleMessageListenerContainer.setMessageConverter(jackson2JsonMessageConverter);
            String beanName=String.format("%s-%s", ListenerContainerId==null||ListenerContainerId.isEmpty()?
                    UUID.randomUUID().toString():ListenerContainerId,key);
            MessageListenerContainers.put(beanName, simpleMessageListenerContainer);
            if(callback!=null)callback.accept(simpleMessageListenerContainer);
            if (autoStartUp) {
                simpleMessageListenerContainer.start();
                if (log.isDebugEnabled()) {
                    log.debug("=> Message Listener for queues-[{}] has Started.",
                            StringUtils.join(simpleMessageListenerContainer.getQueueNames(), ","));
                }
            } else {
                if(log.isDebugEnabled()){
                    log.debug("=> Message Listener for queues-[{}] has Registed.",
                            StringUtils.join(simpleMessageListenerContainer.getQueueNames(), ","));
                }
            }

        });
    }
    
	@Override
	protected Object determineCurrentLookupKey() {
		return SimpleResourceHolder.get(this);
	}

	private CachingConnectionFactory createRabbitConnectionFactory(String beanName,RabbitProperties config){
        CachingConnectionFactory  connectionFactory=null;
        try{
            connectionFactory  = rabbitConnectionFactory(config);
            this.addTargetConnectionFactory(beanName,connectionFactory);
            rabbitSites.add(beanName);
            if(log.isDebugEnabled()){
                log.debug("=> Rabbit datasource [{}] has Registed.",beanName);
            }
//            this.createSimpleMessageListenerContainer(beanName, connectionFactory);
          }catch(Exception e){
              
          }
        return connectionFactory;
    }

    private CachingConnectionFactory rabbitConnectionFactory(RabbitProperties config)
                throws Exception {
            RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();
            if (config.determineHost() != null) {
                factory.setHost(config.determineHost());
            }
            factory.setPort(config.determinePort());
            if (config.determineUsername() != null) {
                factory.setUsername(config.determineUsername());
            }
            if (config.determinePassword() != null) {
                factory.setPassword(config.determinePassword());
            }
            if (config.determineVirtualHost() != null) {
                factory.setVirtualHost(config.determineVirtualHost());
            }
            if (config.getRequestedHeartbeat() != null) {
                factory.setRequestedHeartbeat(config.getRequestedHeartbeat());
            }
            RabbitProperties.Ssl ssl = config.getSsl();
            if (ssl.isEnabled()) {
                factory.setUseSSL(true);
                if (ssl.getAlgorithm() != null) {
                    factory.setSslAlgorithm(ssl.getAlgorithm());
                }
                factory.setKeyStore(ssl.getKeyStore());
                factory.setKeyStorePassphrase(ssl.getKeyStorePassword());
                factory.setTrustStore(ssl.getTrustStore());
                factory.setTrustStorePassphrase(ssl.getTrustStorePassword());
            }
            if (config.getConnectionTimeout() != null) {
                factory.setConnectionTimeout(config.getConnectionTimeout());
            }
            factory.afterPropertiesSet();
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory(
                    factory.getObject());
            connectionFactory.setAddresses(config.determineAddresses());
            connectionFactory.setPublisherConfirms(config.isPublisherConfirms());
            connectionFactory.setPublisherReturns(config.isPublisherReturns());
            if (config.getCache().getChannel().getSize() != null) {
                connectionFactory
                        .setChannelCacheSize(config.getCache().getChannel().getSize());
            }
            if (config.getCache().getConnection().getMode() != null) {
                connectionFactory
                        .setCacheMode(config.getCache().getConnection().getMode());
            }
            if (config.getCache().getConnection().getSize() != null) {
                connectionFactory.setConnectionCacheSize(
                        config.getCache().getConnection().getSize());
            }
            if (config.getCache().getChannel().getCheckoutTimeout() != null) {
                connectionFactory.setChannelCheckoutTimeout(
                        config.getCache().getChannel().getCheckoutTimeout());
            }
            return connectionFactory;
        }
     
}
