/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import org.springframework.data.redis.core.RedisTemplate;

import com.xflib.framework.utils.SpringUtils;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRedisHolder {

    private static InheritableThreadLocal<String> siteContextHolder = 
            new InheritableThreadLocal<String>(); 
    private static InheritableThreadLocal<String> sourceContextHolder = 
            new InheritableThreadLocal<String>(); 

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

    public static RedisTemplate<String, Object> getRedisTemplateBySite(String site) {
        return getRedisTemplate(site,null);
    }

    public static RedisTemplate<String, Object> getRedisTemplateBySource(String source) {
        return getRedisTemplate(null,source);
    }
     public static RedisTemplate<String, Object> getRedisTemplate() {
        return getRedisTemplate(null,null);
    }
    
    @SuppressWarnings("unchecked")
    public static RedisTemplate<String, Object> getRedisTemplate(String site, String source) {
        if(site!=null && !site.isEmpty())DynamicRedisHolder.setSite(site);
        if(source!=null && !source.isEmpty())DynamicRedisHolder.setSource(source);
        return SpringUtils.getBean("redisTemplate", RedisTemplate.class);
    }

}
