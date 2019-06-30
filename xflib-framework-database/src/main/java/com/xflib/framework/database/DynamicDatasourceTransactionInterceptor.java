/**Copyright: Copyright (c) 2016, Koradji */
package com.xflib.framework.database;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.xflib.framework.database.annotation.DataSource;
import com.xflib.framework.database.utils.DynamicDataSourceHolder;

//import lombok.extern.slf4j.Slf4j;

/**
 * 依据@DataSource为Transaction重设数据源
 * 忽略了类上的DataSource，而只检测类方法上的DataSource和接口类及方法上的@DataSource
 * 
 * History:<br> 
 *    . v1.0.0.20180120, Koradji, Fixed: 将事务结束后清除数据员改为回复原数据源<br>
 *    . v1.0.0.20161106, Koradji, Create<br>
 */
@SuppressWarnings("serial")
public class DynamicDatasourceTransactionInterceptor extends TransactionInterceptor {

	@Autowired
	DataSource dataSource;
	
	@SuppressWarnings("deprecation")
    @Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
        long ltime = System.currentTimeMillis();
		String re="master"; 
//        Object target = invocation.getThis();
		Class<?> target = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Method m=invocation.getMethod();
    	if(m.isAnnotationPresent(DataSource.class)) {
    		// 如果方法上定义了@DataSource
            DataSource data = m.getAnnotation(DataSource.class);
            re=data.value();
    	}else{
    		// 检查接口是否定义了@DataSource
	        String method = invocation.getMethod().getName();
	
	        Class<?>[] classz = target.getClass().getInterfaces();
	        Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
		
	        int index=0;
	        while("master".equals(re) && classz.length>index){
			    try {
		            m = classz[index].getMethod(method, parameterTypes);
			        if (method != null){
			        	if(m.isAnnotationPresent(DataSource.class)) {
				        	// 如果接口方法上定义了@DataSource
				            DataSource data = m.getAnnotation(DataSource.class);
				            re=data.value();
				        } else  {
					        if (classz[index] != null && classz[index].isAnnotationPresent(DataSource.class)) {
					        	// 如果接口类定义了@DataSource
					            DataSource data = classz[index].getAnnotation(DataSource.class);
					            re=data.value();
					        }
					    }
			        }
			    } catch (Exception e) {
			    	 /*logger.error(String.format(" An error has occurred at %s when get dynamic dataSource:%s", 
				        		StackTraceUtils.getMethodFullName(), System.getProperty("line.separator"), e.toString()));*/
			    }
	        }
    	}
    	
    	String oldDs=DynamicDataSourceHolder.getDataSourceKey(); 
    	if(!oldDs.isEmpty())oldDs=oldDs.substring(1);
    	
	    DynamicDataSourceHolder.setDataSourceKey(re);
	    Object rt = null;
        long ltime1 = System.currentTimeMillis();
        long execTime1 = 0;
	    try{
	    	rt=super.invoke(invocation);
	        execTime1 = System.currentTimeMillis() - ltime1;
	    }catch(Exception e){
	    	throw new Throwable();
	    }finally {
	    	DynamicDataSourceHolder.setDataSourceKey(oldDs);
		}
        long execTime = System.currentTimeMillis() - ltime;
        
        if(logger.isDebugEnabled()){
        	String msg="=>"+this.getClass().getSimpleName()  + " Execution time = " + execTime + "ms but invoke() Execution time = " + execTime1 + "ms";
        	logger.debug(msg);
        }
        
        return rt;
	    
	  } 


}
