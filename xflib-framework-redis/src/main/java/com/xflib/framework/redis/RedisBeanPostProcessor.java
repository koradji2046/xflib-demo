package com.xflib.framework.redis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 自定义的后置处理器
 * Created by smlz on 2019/4/3.
 */
public class RedisBeanPostProcessor implements BeanPostProcessor{

	private RedisConnectionFactory redisConnectionFactory;
	
	public RedisBeanPostProcessor(RedisConnectionFactory redisConnectionFactory){
		this.redisConnectionFactory=redisConnectionFactory;
	}
	
	@Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
    }

	@SuppressWarnings("unchecked")
	@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof StringRedisTemplate) {
            StringRedisTemplate stringRedisTemplate = (StringRedisTemplate) bean;
            stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
            return stringRedisTemplate;
        }else if(bean instanceof RedisTemplate){
            @SuppressWarnings("rawtypes")
			RedisTemplate redisTemplate = (RedisTemplate) bean;
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            @SuppressWarnings("rawtypes")
			Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);
            StringRedisSerializer ser = new StringRedisSerializer();
            redisTemplate.setKeySerializer(ser);
            redisTemplate.setHashKeySerializer(ser);
            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            redisTemplate.afterPropertiesSet();
            return redisTemplate;
        }
        return bean;
    }

}
