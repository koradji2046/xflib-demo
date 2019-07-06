/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.configure;

import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;

import com.xflib.framework.redis.JsonRedisTemplate;
import com.xflib.framework.redis.jedis.DynamicJedisConnectionFactory;
import com.xflib.framework.redis.jedis.configure.DynamicRedisProperties;
import com.xflib.framework.redis.jedis.utils.DynamicRedisHolder;

import redis.clients.jedis.Jedis;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
public class JsonRedisTemplateConfiguration {

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
