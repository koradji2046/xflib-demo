/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.utils;


import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Spring帮助类
 *
 * History:<br> 
 *    . 1.0.0.20171225, com.qzdatasoft.koradji, fixed: 去掉无用的无参构造函数<br>
 *    . 1.0.0.20160910, com.qzdatasoft.koradji, Create<br>
 *
 */
@Component
public class SpringUtils implements ApplicationContextAware {
// 1.0.0.20171225,koradaji, start
//	public SpringUtils(){
//		System.out.println("configLoader->"+this.getClass().getSimpleName());		
//	}
// 1.0.0.20171225,koradaji, end
	
	public static WebApplicationContext getWebApplicationContext() {
		return (WebApplicationContext) context;
//		return WebApplicationContextUtils.getRequiredWebApplicationContext(HttpRequestContextHelper.getRequest().getSession().getServletContext());
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static Object getBean(String id) {
		return getWebApplicationContext().getBean(id);
	}
	
	public static <T> T getBean(String id, Class<T> requiredType) {
		return getWebApplicationContext().getBean(id,requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return getWebApplicationContext().getBean(requiredType);
	}
	
	/**
	 * 获取应用程序根目录
	 * @param request	request
	 * @return	String
	 */
	public static String getApplicationBaseUrl(HttpServletRequest request) {
		
		String path = request.getContextPath();
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";	
		
		return basePath;
	}
	
	/**
	 * 获取资源目录路径即WEB-INF/classes
	 * 貌似测试时得不到根路径，返回的是test-classes,这不是我要的，为什么？
	 * @return 非web环境下返回运行程序的根路径
	 */
	public static String getClassPath() {
		String r="";
		try{
			r=Thread.currentThread().getContextClassLoader().getResource("/").getPath(); 
		}catch(Exception e){
			r=Thread.currentThread().getContextClassLoader().getResource("").getPath();
		}
		if(StringUtils.isNotBlank(r)){
			// 解决 linux 下面的Bug
			if(!r.startsWith("/")){
				r=r.substring(1);
			}
		}
		
		return r;
		
	}
	
	
	public  static String getBeanName(Class clazz) {
        String className = clazz.getSimpleName();// 获取类名
        String big = className.substring(0, 1);// 获取首字母（类名首字母大写）
        String small = big.toLowerCase();// 将首字母变为小写
        return small + className.substring(1);// 获得已小写字母开头的类名
    }

	/**
	 * 将类注册到Spring容易，交Spring进行管理<br>
	 * @param beanId
	 * @param className
	 */
	public static void registerBean(String beanId,String className) {
		registerBean(beanId, className, null, null, null);
	}
    public static void registerBean(String beanId,Class<?> clazz) {
        registerBean(beanId, clazz.getName(), null, null, null);
    }
    public static void registerBean(String beanId, Class<?> clazz, Map<String, Object> args) {
        registerBean(beanId,clazz.getName(), args,null, null); 
    }
    public static void registerBean(String beanId, Class<?> clazz, Map<String, Object> args, 
            Map<String, Object> properties) {
        registerBean(beanId,SpringUtils.getBeanName(clazz), args,properties, null); 
    }
    public static void registerBean(String beanId, Class<?> clazz, Map<String, Object> args, 
            Map<String, Object> properties, Function<BeanDefinitionBuilder,Boolean> callback) {
        registerBean(beanId,SpringUtils.getBeanName(clazz), args,properties, callback); 
    }
    public static void registerBean(String beanId,String className, Map<String, Object> args) {
        registerBean(beanId,className, args,null, null); 
    }
    public static void registerBean(String beanId,String className, Map<String, Object> args, 
            Map<String, Object> properties) {
        registerBean(beanId,className, args,properties, null); 
    }
	public static void registerBean(String beanId,String className, Map<String, Object> args, 
	        Map<String, Object> properties, Function<BeanDefinitionBuilder,Boolean> callback) {
		
		// get the BeanDefinitionBuilder
		BeanDefinitionBuilder beanDefinitionBuilder = 	BeanDefinitionBuilder.genericBeanDefinition(className.trim());  
		
		// 注册benan属性
		if(properties!=null){
			for(Map.Entry<String, Object> e:properties.entrySet()){
				beanDefinitionBuilder.addPropertyValue(e.getKey(), e.getValue());
			}
		}
//        if(args!=null){
//            for(Map.Entry<String, Object> e:args.entrySet()){
//                beanDefinitionBuilder.addConstructorArgValue(e.getValue());
//            }
//        }
		
		// 调用callback
		if(callback!=null)
			callback.apply(beanDefinitionBuilder);
		
		// get the BeanDefinition
		BeanDefinition beanDefinition=beanDefinitionBuilder.getBeanDefinition();
		// 注册benan初始化参数
		if(args!=null)
			for(Map.Entry<String, Object> e:args.entrySet()){
				beanDefinition.setAttribute(e.getKey(), e.getValue());
			}
		
		// register the bean
		getBeanDefinitionRegistry().registerBeanDefinition(beanId,beanDefinition);  
	}
	
	public static void unregisterBean(String beanId){
		getBeanDefinitionRegistry().removeBeanDefinition(beanId);
	}

//	@Autowired
	private static ApplicationContext context;

	public static BeanDefinitionRegistry getBeanDefinitionRegistry(){
		ConfigurableApplicationContext configurableContext = 
				(ConfigurableApplicationContext) getWebApplicationContext();  
		BeanDefinitionRegistry beanDefinitionRegistry = 
				(DefaultListableBeanFactory) configurableContext.getBeanFactory();  
		return beanDefinitionRegistry;
	}
	
	public static boolean BeanExist(String beanId){
		return getWebApplicationContext().containsBeanDefinition(beanId);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		System.out.println("configLoader->context assigned - "+this.getClass().getSimpleName());		
		context=applicationContext;
	}
	
}
