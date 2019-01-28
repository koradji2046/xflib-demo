/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration.redis;

import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xflib.framework.redis.DynamicRedisConnectionFactory;
import com.xflib.framework.redis.DynamicRedisProperties;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@EnableConfigurationProperties({ DynamicRedisProperties.class,RedisProperties.class })
public class DynamicRedisConfiguration {

    public DynamicRedisConfiguration(RedisProperties properties){
    }
    
    @Bean("redisConnectionFactory")
    @ConditionalOnMissingBean(name = "redisConnectionFactory")
    public DynamicRedisConnectionFactory dynamicRedisConnectionFactory(
            DynamicRedisProperties dynamicProperties) {
        return new DynamicRedisConnectionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(DynamicRedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisTemplate")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public RedisTemplate<String, Object> redisTemplate(DynamicRedisConnectionFactory redisConnectionFactory) {
        RedisTemplate template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer ser = new StringRedisSerializer();
        template.setKeySerializer(ser);
        template.setHashKeySerializer(ser);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

}
