/**Copyright: Copyright (c) 2017, koradji*/
package com.xflib.framework.utils;

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
import org.springframework.web.context.WebApplicationContext;

/**
 * Spring帮助类
 *
 * History:<br>
 * . 1.0.0.20171225, com.qzdatasoft.koradji, fixed: 去掉无用的无参构造函数<br>
 * . 1.0.0.20160910, com.qzdatasoft.koradji, Create<br>
 *
 */
public class SpringUtils implements ApplicationContextAware {

    public static WebApplicationContext getWebApplicationContext() {
        return (WebApplicationContext) context;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
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
     * 获取应用程序根目录
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

    /**
     * 获取资源目录路径即WEB-INF/classes 貌似测试时得不到根路径，返回的是test-classes,这不是我要的，为什么？
     * 
     * @return 非web环境下返回运行程序的根路径
     */
    public static String getClassPath() {
        String r = "";
        try {
            r = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        } catch (Exception e) {
            r = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        }
        if (StringUtils.isNotBlank(r)) {
            // 解决 linux 下面的Bug
            if (!r.startsWith("/")) {
                r = r.substring(1);
            }
        }

        return r;

    }

    @SuppressWarnings("rawtypes")
    public static String getBeanName(Class clazz) {
        String className = clazz.getSimpleName();// 获取类名
        String big = className.substring(0, 1);// 获取首字母（类名首字母大写）
        String small = big.toLowerCase();// 将首字母变为小写
        return small + className.substring(1);// 获得已小写字母开头的类名
    }

    @SuppressWarnings("rawtypes")
    public static void registerBean(Class clazz) {
        registerBean(clazz,null);
    }
    
    @SuppressWarnings("rawtypes")
    public static void registerBean(Class clazz, Function<BeanDefinitionBuilder, Boolean> callback) {
        String beanId=getBeanName(clazz);
        registerBean(beanId,clazz,callback);
    }
    
    @SuppressWarnings("rawtypes")
    public static void registerBean(String beanId, Class clazz) {
        registerBean(beanId,clazz,null);
    }
    
    @SuppressWarnings("rawtypes")
    public static void registerBean(String beanId, Class clazz, Function<BeanDefinitionBuilder, Boolean> callback) {
        // get the BeanDefinitionBuilder
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);

        // 调用callback
        if (callback != null && !callback.apply(beanDefinitionBuilder)){
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

    public static boolean BeanExist(String beanId) {
        return getWebApplicationContext().containsBeanDefinition(beanId);
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

    private static ApplicationContext context;

}
