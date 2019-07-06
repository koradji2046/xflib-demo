/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.jedis.utils;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRedisHolder implements ApplicationContextAware {

	private static ApplicationContext context;
	private static InheritableThreadLocal<String> siteContextHolder = new InheritableThreadLocal<String>();
	private static InheritableThreadLocal<String> sourceContextHolder = new InheritableThreadLocal<String>();

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static void setSite(String site) {
		siteContextHolder.set(site);
	}

	public static String getSite() {
		String site = siteContextHolder.get();
		return (site == null || site.isEmpty()) ? "default" : site;
	}

	public static void setSource(String source) {
		sourceContextHolder.set(source);
	}

	public static String getSource() {
		String source = sourceContextHolder.get();
		return (source == null || source.isEmpty()) ? "default" : source;
	}

	public static void setContext(String site, String source) {
		siteContextHolder.set(site);
		sourceContextHolder.set(source);
	}

	public static String getContext() {
		String site = DynamicRedisHolder.getSite();
		site = (site == null || site.isEmpty() ? "default" : site);
		String source = DynamicRedisHolder.getSource();
		source = (source == null || source.isEmpty() ? "default" : source);
		return String.format("redis-%s-%s", site, source);
	}

	public static void removeContext() {
		sourceContextHolder.remove();
		siteContextHolder.remove();
	}

	/**
	 * 根据指定的RedisTemplate类型获取适用于当前site及source的redisTemplate
	 * 
	 * @param type  - RedisTemplate<K,V>或其子类
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T extends RedisTemplate> T getRedisTemplate(Class<T> type) {
		return getRedisTemplate(type, null, null);
	}

	/**
	 * 根据指定的RedisTemplate类型及source获取适用于当前site的redisTemplate
	 * @param type - RedisTemplate<K,V>或其子类
	 * @param source - site下定义的某个redis数据源
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T  extends RedisTemplate> T getRedisTemplate(Class<T> type, String source) {
		return getRedisTemplate(type, null, source);
	}

    @SuppressWarnings("rawtypes")
	public static <T  extends RedisTemplate> T getRedisTemplateBySite(Class<T> type, String site) {
      return getRedisTemplate(type, site, null);
    }
    
    @SuppressWarnings("rawtypes")
	public static <T  extends RedisTemplate> T getRedisTemplate(Class<T> type, String site, String source) {
        if(site!=null && !site.isEmpty())DynamicRedisHolder.setSite(site);
        if(source!=null && !source.isEmpty())DynamicRedisHolder.setSource(source);
        return context.getBean( type);
    }

    
    /**
     * 使用指定的redisTemplate在当前site和指定的source上执行callback，执行完毕会恢复原来的source
     * 
     * @param type      resiaTemplate类，如redisTemplate<Object,Object>|StringRedisTemplate|JsonRedisTemplate
     * @param source   目标site的redis
     * @param callback 回调方法
     */
   @SuppressWarnings("rawtypes")
	public static <T  extends RedisTemplate> void tryEexecuteByRedisTemplate(Class<T> type, String source,Consumer <T> callback){
    	tryEexecuteByRedisTemplate(type,null,source,callback);
    }
    /**
     * 使用指定的redisTemplate在指定site和source上执行callback，执行完毕会恢复原来的site和source
     * 
     * @param type      resiaTemplate类，如redisTemplate<Object,Object>|StringRedisTemplate|JsonRedisTemplate
     * @param site        目标site
     * @param source   目标site的redis
     * @param callback 回调方法
     */
    @SuppressWarnings("rawtypes")
	public static <T  extends RedisTemplate> void tryEexecuteByRedisTemplate(Class<T> type, String site, String source,Consumer <T> callback) {
    	
    	String _site=DynamicRedisHolder.getSite();
    	String _source=DynamicRedisHolder.getSource();
        Optional<T> redisTemplate= Optional.ofNullable(getRedisTemplate( type,site,source));
        
        if(redisTemplate.isPresent()){
	        try{
	        	callback.accept(redisTemplate.get());
	        }finally{
	        	DynamicRedisHolder.setContext(_site, _source);
	        }
        }
     }
}
