/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xflib.framework.redis.jedis.utils.DynamicRedisHolder;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
//@AutoConfigureBefore(RedisAutoConfiguration.class)
//@EnableConfigurationProperties({ DynamicRedisProperties.class,RedisProperties.class })
public class DynamicRedisHolderConfiguration {

//    public JsonRedisTemplateConfiguration(
//            RedisProperties properties,
//            DynamicRedisProperties dynamicProperties){
//    }
    
//    @Bean("redisConnectionFactory")
//    @ConditionalOnMissingBean(name = "redisConnectionFactory")
//    @ConditionalOnClass({DynamicJedisConnectionFactory.class,Jedis.class, RedisOperations.class})
//    public DynamicJedisConnectionFactory dynamicJedisConnectionFactory() {
//        return new DynamicJedisConnectionFactory();
//    }

//    @Bean
//    @ConditionalOnMissingBean(JsonRedisTemplate.class)
//    public JsonRedisTemplate jsonRedisTemplate(
//    		RedisConnectionFactory redisConnectionFactory) 
//    				throws UnknownHostException {
//		JsonRedisTemplate template = new JsonRedisTemplate();
//		template.setConnectionFactory(redisConnectionFactory);
//		return template;
//    }
	
    @Bean
    public DynamicRedisHolder dynamicRedisHolder(){
    	return new DynamicRedisHolder();
    }
    
}
