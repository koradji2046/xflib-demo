//package com.xflib.framework.database;
//
//import java.lang.reflect.Method;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.xflib.framework.database.annotation.DataSource;
//
///**
// * 这是一个基于接口的Spring AOP实现的动态代理 为什么不使用基于实现类的CGLib实现动态代理呢？因为效率比Spring
// * AOP低，除非使用CGLib静态代理
// * 
// * @author koradji
// *
// */
//@Aspect
//public class DynamicDatasourceAspect {
//	private Logger logger = LoggerFactory.getLogger(DynamicDatasourceAspect.class);
//	protected static final InheritableThreadLocal<String> preDatasourceHolder = new InheritableThreadLocal<>();
//	static final String DATASOURCE_CLASS_NAME = "@annotation(com.xflib.framework.database.annotation.DataSource)";
//
//	@Pointcut(DATASOURCE_CLASS_NAME)
//	protected void datasourceAspect() {
//	}
//
//	@Before("datasourceAspect()")
//	public void before(JoinPoint jp) {
//		try {
//			preDatasourceHolder.set(DynamicDataSourceHolder.getSource());
//			DynamicDataSourceHolder.setSource(determineDatasource(jp));
//		} catch (Exception e) {
//			// TODO:
//		}
//	}
//	
//    @Around("datasourceAspect()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        return joinPoint.proceed();
//    }
//
//    @After("datasourceAspect()")
//	public void after() {
//		try{
//			DynamicDataSourceHolder.setSource(preDatasourceHolder.get());
//			preDatasourceHolder.remove();
//		} catch (Exception e) {
//			// TODO:
//		}
//	}
//
//	/**
//	 * 根据@TargetDataSource的属性值设置不同的dataSourceKey,以供DynamicDataSource
//	 */
//	private String determineDatasource(JoinPoint jp) throws NoSuchMethodException, SecurityException {
//		// 设置默认数据源
//		String re = "master";
//
//		// 获取当前调用方法
//		Class<?> clazz = jp.getSignature().getDeclaringType();
//		String methodName = jp.getSignature().getName();
//		String mRe=resolveDataSourceFromMethod(clazz, methodName);
//		if(null!=mRe) {
//			re=mRe;
//		}else{
//			mRe= resolveDataSourceFromClass(clazz, methodName);
//			if(null!=mRe){
//				re=mRe;
//			}
//		}
//		
//		logger.debug("==>Current DataSource is {}.", re);
//		
//		return re;
//	}
//
//	// 根据类获取类上的@DataSource, 不存在时返回null
//	private String resolveDataSourceFromClass(Class<?> clazz, String methodName ) {
//		Class<?> searchType = clazz;
//		while (searchType != null) {
//			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
//			for (Method method : methods) {
//				if (methodName.equals(method.getName())) {
//					if( method.getClass().isAnnotationPresent(DataSource.class)){
//						return resolveDataSourceName(method.getClass().getAnnotation(DataSource.class));
//					}
//				}
//			}
//			searchType = searchType.getSuperclass();
//		}
//		return null;
//	}
//
//	// 根据类和方法名获取方法上的@DataSource, 不存在时返回null
//	private String resolveDataSourceFromMethod(Class<?> clazz, String methodName) {
//		Method m = findUniqueMethod(clazz, methodName);
//		if (m != null) {
//			DataSource choDs = m.getAnnotation(DataSource.class);
//			return resolveDataSourceName(choDs);
//		}
//		return null;
//	}
//
//	// 根据类和方法名获取Method
//	private static Method findUniqueMethod(Class<?> clazz, String name) {
//		Class<?> searchType = clazz;
//		while (searchType != null) {
//			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
//			for (Method method : methods) {
//				if (name.equals(method.getName())) {
//					return method;
//				}
//			}
//			searchType = searchType.getSuperclass();
//		}
//		return null;
//	}
//
//	// 获得DataSource定义的值
//	private String resolveDataSourceName(DataSource ds) {
//		return ds == null ? null : ds.value();
//	}
//
//// 原始方法
////	public String determineDatasource(JoinPoint jp) throws NoSuchMethodException, SecurityException {
////
////		// 设置默认数据源
////		String re = "master";
////
////		// 获取当前调用方法
////		Class<?> tt = jp.getSignature().getDeclaringType();
////		String method = jp.getSignature().getName();
////		Object[] args = jp.getArgs();
////		Class<?>[] parameterTypes = new Class<?>[args.length];
////		for (int i = 0; i < args.length; i++) {
////			parameterTypes[i] = args[i].getClass();
////		}
////		Method m = tt.getMethod(method, parameterTypes);
////
////		if (m.isAnnotationPresent(DataSource.class)) {
////			// 如果方法上定义了@DataSource
////			DataSource data = m.getAnnotation(DataSource.class);
////			re = data.value();
////		} else if (!tt.isInterface()) {
////			// 检查接口是否定义了@DataSource
////			Class<?>[] classz = tt.getClass().getInterfaces();
////
////			int index = 0;
////			boolean found=false;
////			String _re="";
////			while ("master".equals(re) && classz.length > index) {
////				try {
////					m = classz[index].getMethod(method, parameterTypes);
////					if (method != null) {
////						if (m.isAnnotationPresent(DataSource.class)) {
////							// 如果接口方法上定义了@DataSource
////							DataSource data = m.getAnnotation(DataSource.class);
////							re = data.value();
////							found=true;
////							break;
////						} 
////						else {
////							if (classz[index] != null && classz[index].isAnnotationPresent(DataSource.class)) {
////								// 如果接口类定义了@DataSource
////								DataSource data = classz[index].getAnnotation(DataSource.class);
////								_re = data.value();
////							}
////						}
////					}
////				} catch (Exception e) {
////					/*
////					 * logger.error(String.
////					 * format(" An error has occurred at %s when get dynamic dataSource:%s"
////					 * , StackTraceUtils.getMethodFullName(),
////					 * System.getProperty("line.separator"), e.toString()));
////					 */
////				}
////			}
////			// 如果方法上没有@DataSource，检查接口类上是否有@DataSource
////			if(!found && !_re.isEmpty()){
////				re = _re;
////				found=true;
////			}
////			// 如果方法和接口类上都没有@DataSource，则检查当前实现类上是否有@DataSource
////			if(!found && tt.isAnnotationPresent(DataSource.class)){
////				// 检查当前实现类上是否定义了@DataSource
////				DataSource data = tt.getAnnotation(DataSource.class);
////				re = data.value();
////			}
////		} else if (tt.isAnnotationPresent(DataSource.class)) {
////			// 检查接口类上是否定义了@DataSource
////			DataSource data = tt.getAnnotation(DataSource.class);
////			re = data.value();
////		}
////		return re;
////	}
//
//}