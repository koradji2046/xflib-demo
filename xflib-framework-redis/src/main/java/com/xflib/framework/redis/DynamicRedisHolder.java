/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.xflib.framework.common.BaseException;
import com.xflib.framework.utils.SpringUtils;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRedisHolder {

    private static InheritableThreadLocal<String> siteContextHolder = 
            new InheritableThreadLocal<String>(); 

    public static void setSite(String site) {
        siteContextHolder.set(site);
    }

    public static String getSite() {
        String site = siteContextHolder.get();
        return (site == null || site.isEmpty()) ? "default" : site;
    }

    public static RedisConnectionFactory getRedisConnectionFactoryContext() {
        String site = DynamicRedisHolder.getSite();
        String lookupKey = String.format("redisConnectionFactory-%s", site.isEmpty() ? "default" : site);
        RedisConnectionFactory result = (SpringUtils.getBean(lookupKey, RedisConnectionFactory.class));
        if (result == null) {
            throw new BaseException(String.format("redis.context.notfound.%s", lookupKey));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static RedisTemplate<String, Object> getRedisTemplate(String site) {
        DynamicRedisHolder.setSite(site);
        return SpringUtils.getBean("redisTemplate", RedisTemplate.class);
    }
}
