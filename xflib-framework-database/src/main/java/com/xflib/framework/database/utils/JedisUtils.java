/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.database.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Jedis工具类<br>
 * <br>
 * History:<br> 
 *    . v1.0.0.20180518, com.qzdatasoft.jianli, Create<br>
 *    . v主版本号.次版本号.分支版本.20180518, com.qzdatasoft.jianli, Fixed|Added|Deprecated: describe<br>
 *    . v主版本号.次版本号.分支版本.20180518, com.qzdatasoft.jianli, Create<br>
 */
@Deprecated
public class JedisUtils implements ApplicationContextAware {
	
//	private static final int ArrayList = 0;
	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext context)throws BeansException{
		if(JedisUtils.applicationContext == null){
			JedisUtils.applicationContext = context;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public <T>DefaultRedisScript redisScript2(Class<T> type, String script){
		DefaultRedisScript<T> redisScript=new DefaultRedisScript<>();
		redisScript.setScriptText(script);
		redisScript.setResultType(type);
		return redisScript;
	}

	@SuppressWarnings("rawtypes")
	public static RedisTemplate redisTemplate(){
		RedisTemplate redisTemplate=(RedisTemplate) applicationContext.getBean("jsonRedisTemplate"/*,RedisTemplate.class*/);
		return redisTemplate;
	}
	
	/**
	 * 缓存是否存在<br>
	 * 
	 * @param key  缓存key值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean exists(String key,String hashKey){
		HashOperations<String,String,Long> ops = redisTemplate().opsForHash();
		return ops.hasKey(key, hashKey);
	}
	
	
	/**
	 * 设置缓存<br>
	 * 
	 * @param key  缓存key值
	 * @param value  缓存值(数据)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean set(String key,String hashKey,Long value){
		boolean exist = false;
		try{
			HashOperations<String,String,Long> ops = redisTemplate().opsForHash();
			ops.put(key, hashKey, value);
			exist=true;
		}finally{
		}
		return exist;
	} 
	
	
	/**
	 * 缓存是否存在<br>
	 * 
	 * @param key  缓存key值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Long incr(String key,String hashKey){
		HashOperations<String,String,Long> ops = redisTemplate().opsForHash();
		return ops.increment(key,hashKey, 1L);
	}

}
