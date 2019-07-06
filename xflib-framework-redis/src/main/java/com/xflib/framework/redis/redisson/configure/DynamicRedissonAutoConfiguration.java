/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.redisson.configure;

import java.io.IOException;
import java.net.UnknownHostException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.xflib.framework.redis.JsonRedisTemplate;
import com.xflib.framework.redis.jedis.utils.DynamicRedisHolder;
import com.xflib.framework.redis.redisson.DynamicRedisson;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@ConditionalOnClass({Redisson.class, RedisOperations.class})
@AutoConfigureBefore(RedisAutoConfiguration.class)
@EnableConfigurationProperties({ DynamicRedissonProperties.class,RedissonProperties.class, RedisProperties.class })
public class DynamicRedissonAutoConfiguration {

    public DynamicRedissonAutoConfiguration(
            RedisProperties properties,
            DynamicRedissonProperties dynamicProperties){
    }
    
  @Bean
  public DynamicRedisHolder dynamicRedisHolder(){
  	return new DynamicRedisHolder();
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

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }
    
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(RedissonClient.class)
    public RedissonClient redissonClient() throws IOException {
    	return new DynamicRedisson();
    }

}
