/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.amqp;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.AbstractRoutingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRabbitConnectionFactory extends AbstractRoutingConnectionFactory{

    private Logger log = LoggerFactory.getLogger(DynamicRabbitConnectionFactory.class);

    @Autowired
    private DynamicRabbitProperties dynamicRabbitProperties;

    @Autowired
    private RabbitProperties defaultRabbitProperties;

    // redisContext list
    List<String> redisContexts=new ArrayList<>();
    
    @PostConstruct
    public void createRedisConnectionFactory() throws UnknownHostException {
        
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
    
	@Override
	protected Object determineCurrentLookupKey() {
		return SimpleResourceHolder.get(this);
	}

	private CachingConnectionFactory createRabbitConnectionFactory(String beanName,RabbitProperties config){
        CachingConnectionFactory  connectionFactory=null;
        try{
            connectionFactory  = rabbitConnectionFactory(config);
            this.addTargetConnectionFactory(beanName,connectionFactory);
            redisContexts.add(beanName);
            if(log.isDebugEnabled()){
                log.debug("=> Rabbit datasource [{}] has Registed.",beanName);
            }
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
