/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

	public static <T> T getRedisTemplateBySite(Class<T> type, String site) {
		return getRedisTemplate(type, site, null);
	}

	public static <T> T getRedisTemplateBySource(Class<T> type, String source) {
		return getRedisTemplate(type, null, source);
	}

	public static <T> T getRedisTemplate(Class<T> type) {
		return getRedisTemplate(type, null, null);
	}

    public static <T> T getRedisTemplate(Class<T> type, String site, String source) {
        if(site!=null && !site.isEmpty())DynamicRedisHolder.setSite(site);
        if(source!=null && !source.isEmpty())DynamicRedisHolder.setSource(source);
        return context.getBean( type);
    }

}
