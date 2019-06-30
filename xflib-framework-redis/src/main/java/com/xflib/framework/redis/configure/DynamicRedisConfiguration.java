/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.configure;

import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;

import com.xflib.framework.redis.DynamicJedisConnectionFactory;
import com.xflib.framework.redis.JsonRedisTemplate;
import com.xflib.framework.redis.utils.DynamicRedisHolder;

import redis.clients.jedis.Jedis;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@EnableConfigurationProperties({ DynamicRedisProperties.class,RedisProperties.class })
public class DynamicRedisConfiguration {

    public DynamicRedisConfiguration(
            RedisProperties properties,
            DynamicRedisProperties dynamicProperties){
    }
    
    @Bean
    public DynamicRedisHolder dynamicRedisHolder(){
    	return new DynamicRedisHolder();
    }
    
    @Bean("redisConnectionFactory")
    @ConditionalOnMissingBean(name = "redisConnectionFactory")
    @ConditionalOnClass({DynamicJedisConnectionFactory.class,Jedis.class, RedisOperations.class})
    public DynamicJedisConnectionFactory dynamicJedisConnectionFactory() {
        return new DynamicJedisConnectionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(JsonRedisTemplate.class)
    public JsonRedisTemplate jsonRedisTemplate(
    		RedisConnectionFactory redisConnectionFactory) 
    				throws UnknownHostException {
		JsonRedisTemplate template = new JsonRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
    }
	
}
