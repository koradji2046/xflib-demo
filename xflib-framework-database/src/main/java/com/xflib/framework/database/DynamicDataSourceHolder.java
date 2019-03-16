/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.database;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicDataSourceHolder {

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

    @Deprecated
    public static void setDataSourceKey(String source) {
        setSource(source);
    }

    public static void setSource(String source) {
        sourceContextHolder.set(source);
    }
    
    @Deprecated
    public static String getDataSourceKey() {
        return getSource();
    }

    public static String getSource() {
        String source = sourceContextHolder.get();
        return (source == null || source.isEmpty()) ? "default" : source;
    }
    
    public static void setContext(String site, String source) {
        siteContextHolder.set(site);
        sourceContextHolder.set(source);
    }
    
    @Deprecated
    public static String getDataSource() {
        return getContext();
    }
   
    public static String getContext() {
        String site=DynamicDataSourceHolder.getSite();
        site=(site==null||site.isEmpty()?"default":site);
        String source=DynamicDataSourceHolder.getSource();
        source=(source==null||source.isEmpty()?"default":source);
        return String.format("dataSource-%s-%s", site,source);
    }

    public static void removeContext(){
        sourceContextHolder.remove();
        siteContextHolder.remove();
    }

//    public static RedisTemplate<String, Object> getRedisTemplateBySite(String site) {
//        return getRedisTemplate(site,null);
//    }
//
//    public static RedisTemplate<String, Object> getRedisTemplateBySource(String source) {
//        return getRedisTemplate(null,source);
//    }
//    
//     public static RedisTemplate<String, Object> getRedisTemplate() {
//        return getRedisTemplate(null,null);
//    }
//    
//    @SuppressWarnings("unchecked")
//    public static RedisTemplate<String, Object> getRedisTemplate(String site, String source) {
//        if(site!=null && !site.isEmpty())DynamicDatabaseHolder.setSite(site);
//        if(source!=null && !source.isEmpty())DynamicDatabaseHolder.setSource(source);
//        return SpringUtils.getBean("redisTemplate", RedisTemplate.class);
//    }

}
