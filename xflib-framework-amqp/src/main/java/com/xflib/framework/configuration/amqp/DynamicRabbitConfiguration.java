/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration.amqp;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.rabbitmq.client.Channel;
import com.xflib.framework.amqp.DynamicRabbitConnectionFactory;
import com.xflib.framework.amqp.DynamicRabbitListenerContainerFactory;
import com.xflib.framework.amqp.DynamicRabbitProperties;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@ConditionalOnClass({ RabbitTemplate.class, Channel.class })
@EnableConfigurationProperties({ DynamicRabbitProperties.class,RabbitProperties.class })
public class DynamicRabbitConfiguration {

    public DynamicRabbitConfiguration(
            DynamicRabbitProperties dynamicRabbitProperties, 
            RabbitProperties properties){
    }
    
    @Configuration
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public static class RabbitConnectionFactoryCreator{
        
        @Bean("connectionFactory")
        @ConditionalOnMissingBean(name = "connectionFactory")
        public DynamicRabbitConnectionFactory dynamicRabbitConnectionFactory(){
            DynamicRabbitConnectionFactory connectionFactory= new DynamicRabbitConnectionFactory();
            return connectionFactory;
        }
        
        @Bean
        public Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
            Jackson2JsonMessageConverter result= new Jackson2JsonMessageConverter();
            return result;
        }
        
//        @Bean
//        public MessagingMessageConverter messagingMessageConverter(Jackson2JsonMessageConverter payloadConverter){
//            MessagingMessageConverter result= new MessagingMessageConverter();
////            messageConverter.setPayloadConverter(new EventsPayloadMessageConverter());
//            result.setPayloadConverter(payloadConverter);
//            return result;
//        }
        
        @Bean
        public DynamicRabbitListenerContainerFactory dynamicRabbitListenerContainerFactory(){
            DynamicRabbitListenerContainerFactory result=new DynamicRabbitListenerContainerFactory();
            return result;
        }
        
    }
    
    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnProperty(prefix = "spring.rabbitmq", name = "dynamic", matchIfMissing = true)
    @ConditionalOnMissingBean(AmqpAdmin.class)
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    
    
    @Configuration
    @Import(RabbitConnectionFactoryCreator.class)
    protected static class RabbitTemplateConfiguration {

        private final ObjectProvider<MessageConverter> messageConverter;

        private final RabbitProperties properties;

        
//        @Autowired
//        private Jackson2JsonMessageConverter jackson2JsonMessageConverter;
        
        public RabbitTemplateConfiguration(
                ObjectProvider<MessageConverter> messageConverter,
                RabbitProperties properties) {
            this.messageConverter = messageConverter;
            this.properties = properties;
        }

        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        @ConditionalOnMissingBean(RabbitTemplate.class)
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
            
          MessageConverter messageConverter = this.messageConverter.getIfUnique();
            if (messageConverter != null) {
                rabbitTemplate.setMessageConverter(messageConverter);
//                rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
            }
           
            rabbitTemplate.setMandatory(determineMandatoryFlag());
            RabbitProperties.Template templateProperties = this.properties.getTemplate();
            RabbitProperties.Retry retryProperties = templateProperties.getRetry();
            if (retryProperties.isEnabled()) {
                rabbitTemplate.setRetryTemplate(createRetryTemplate(retryProperties));
            }
            if (templateProperties.getReceiveTimeout() != null) {
                rabbitTemplate.setReceiveTimeout(templateProperties.getReceiveTimeout());
            }
            if (templateProperties.getReplyTimeout() != null) {
                rabbitTemplate.setReplyTimeout(templateProperties.getReplyTimeout());
            }
            return rabbitTemplate;
        }

        private boolean determineMandatoryFlag() {
            Boolean mandatory = this.properties.getTemplate().getMandatory();
            return (mandatory != null ? mandatory : this.properties.isPublisherReturns());
        }

        private RetryTemplate createRetryTemplate(RabbitProperties.Retry properties) {
            RetryTemplate template = new RetryTemplate();
            SimpleRetryPolicy policy = new SimpleRetryPolicy();
            policy.setMaxAttempts(properties.getMaxAttempts());
            template.setRetryPolicy(policy);
            ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
            backOffPolicy.setInitialInterval(properties.getInitialInterval());
            backOffPolicy.setMultiplier(properties.getMultiplier());
            backOffPolicy.setMaxInterval(properties.getMaxInterval());
            template.setBackOffPolicy(backOffPolicy);
            return template;
        }

        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        @ConditionalOnProperty(prefix = "spring.rabbitmq", name = "dynamic", matchIfMissing = true)
        @ConditionalOnMissingBean(AmqpAdmin.class)
        public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }

    }

    @Configuration
    @ConditionalOnClass(RabbitMessagingTemplate.class)
    @ConditionalOnMissingBean(RabbitMessagingTemplate.class)
    @Import(RabbitTemplateConfiguration.class)
    protected static class MessagingTemplateConfiguration {

//        @Autowired
//        private  Jackson2JsonMessageConverter messageConverter;
        
        @Bean
        @ConditionalOnSingleCandidate(RabbitTemplate.class)
        public RabbitMessagingTemplate rabbitMessagingTemplate(
                RabbitTemplate rabbitTemplate) {
            RabbitMessagingTemplate result =  new RabbitMessagingTemplate(rabbitTemplate);
//            result.setAmqpMessageConverter(messageConverter);
            
            return result;
        }

    }

}
