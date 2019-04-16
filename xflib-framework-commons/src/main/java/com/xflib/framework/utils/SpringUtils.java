/**Copyright: Copyright (c) 2017, koradji*/
package com.xflib.framework.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * ApplicationContext帮助类
 *
 * History:<br>
 * . 1.0.0.20160910, koradji, Create<br>
 *
 */
public class SpringUtils implements ApplicationContextAware {

	public static boolean BeanExist(String beanId) {
		return context.containsBeanDefinition(beanId);
	}

	public static Object getBean(String id) {
		return getWebApplicationContext().getBean(id);
	}

	public static <T> T getBean(String id, Class<T> requiredType) {
		return getWebApplicationContext().getBean(id, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return getWebApplicationContext().getBean(requiredType);
	}

	/**
	 * 获取应用程序上下文目录
	 * 
	 * @param request
	 *            request
	 * @return String
	 */
	public static String getApplicationBaseUrl(HttpServletRequest request) {

		String path = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path
				+ "/";

		return basePath;
	}

	// /**
	// * 获取资源目录路径即WEB-INF/classes 貌似测试时得不到根路径，返回的是test-classes,这不是我要的，为什么？
	// *
	// * @return 非web环境下返回运行程序的根路径
	// */
	// public static String getClassPath() {
	// String r = "";
	// try {
	// r =
	// Thread.currentThread().getContextClassLoader().getResource("/").getPath();
	// } catch (Exception e) {
	// r =
	// Thread.currentThread().getContextClassLoader().getResource("").getPath();
	// }
	// if (StringUtils.isNotBlank(r)) {
	// // 解决 linux 下面的Bug
	// if (!r.startsWith("/")) {
	// r = r.substring(1);
	// }
	// }
	//
	// return r;
	//
	// }

	/**
	 * 转换标准的短类名(不含包路径)称首字母为小写, 例如: SpringUtils ->springUtils
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getBeanName(Class<?> clazz) {
		String result = "";

		String className = clazz.getSimpleName();// 获取类名
		String big = className.substring(0, 1);// 获取首字母（类名首字母大写）
		String small = big.toLowerCase();// 将首字母变为小写
		result = small + className.substring(1);// 获得已小写字母开头的类名

		return result;
	}

	/**
	 * 转换标准的类名称(含包路径)为简化名称，用于唯一标示或日志，例如 com.xflib.framework.utils.SpringUtils
	 * ->c.x.f.u.SpringUtils
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getBeanFullName(Class<?> clazz) {
		String result = "";

		String[] pkg = clazz.getName().split(".");
		for (int i = 0; i < pkg.length - 1; i++) {
			result += pkg[0] + ".";
		}

		return result + clazz.getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static WebApplicationContext getWebApplicationContext() {
		return (WebApplicationContext) context;
	}

	private static ApplicationContext context;

	// 注册Bean的方法
	public static void registerBean(Class<?> clazz) {
		registerBean(clazz, null);
	}

	public static void registerBean(Class<?> clazz, Function<BeanDefinitionBuilder, Boolean> callback) {
		String beanId = getBeanName(clazz);
		registerBean(beanId, clazz, callback);
	}

	public static void registerBean(String beanId, Class<?> clazz) {
		registerBean(beanId, clazz, null);
	}

	public static void registerBean(String beanId, Class<?> clazz, Function<BeanDefinitionBuilder, Boolean> callback) {
		// get the BeanDefinitionBuilder
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);

		// 调用callback
		if (callback != null && !callback.apply(beanDefinitionBuilder)) {
			return;
		}

		// register the bean
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		getBeanDefinitionRegistry().registerBeanDefinition(beanId, beanDefinition);
	}

	public static void unregisterBean(String beanId) {
		getBeanDefinitionRegistry().removeBeanDefinition(beanId);
	}

	public static BeanDefinitionRegistry getBeanDefinitionRegistry() {
		ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) getWebApplicationContext();
		BeanDefinitionRegistry beanDefinitionRegistry = (DefaultListableBeanFactory) configurableContext
				.getBeanFactory();
		return beanDefinitionRegistry;
	}

	
	// 全局变量存储对象
	private InheritableThreadLocal<ConcurrentHashMap<String, Object>> globalVars=
			new  InheritableThreadLocal<ConcurrentHashMap<String,Object>>();
	
	@SuppressWarnings("unchecked")
	public <T> T getVariable(String key, Class<T> clazz){
		return (T)getVariable(key);
	}
	
	public Object getVariable(String key){
		ConcurrentHashMap<String, Object> map=globalVars.get();
		if(null==map) return null;
		return map.get(key);
	}

	public void setVariable(String key, Object value){
		ConcurrentHashMap<String, Object> map=globalVars.get();
		if(null==map) map=new ConcurrentHashMap<String, Object>();
		map.put(key, value);
		globalVars.set(map);
	}
}
