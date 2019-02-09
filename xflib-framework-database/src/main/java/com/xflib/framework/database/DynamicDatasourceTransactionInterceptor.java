/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.database;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.xflib.framework.common.BaseException;
import com.xflib.framework.database.annotation.DataSource;

//import lombok.extern.slf4j.Slf4j;

/**
 * 为Transaction设置数据源<br>
 * <br>
 * History:<br> 
 *    . v1.0.0.20180120, com.qzdatasoft.Koradji, Fixed: 将事务结束后清除数据员改为回复原数据源<br>
 *    . v1.0.0.20161106, com.qzdatasoft.Koradji, Create<br>
 */
//@Component
@SuppressWarnings("serial")
public class DynamicDatasourceTransactionInterceptor extends TransactionInterceptor {

	@SuppressWarnings("deprecation")
    @Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
        long ltime = System.currentTimeMillis();
//		String re="default"; //v1.0.0.20180120, com.qzdatasoft.Koradji
		String re="master"; //v1.0.0.20180120, com.qzdatasoft.Koradji
        Object target = invocation.getThis();
//        String xxx=DynamicDataSourceHolder.getDataSourceKey();
        Method m=invocation.getMethod();
    	if(m.isAnnotationPresent(DataSource.class)) {
        	// 如果方法上定义了@DynamicDataSource
            DataSource data = m.getAnnotation(DataSource.class);
            re=data.value();
    	}else{        
	        String method = invocation.getMethod().getName();
	
	        Class<?>[] classz = target.getClass().getInterfaces();
	        Class<?>[] parameterTypes = invocation.getMethod().getParameterTypes();
			
		    try {
	            m = classz[0].getMethod(method, parameterTypes);
		        if (method != null){
		        	if(m.isAnnotationPresent(DataSource.class)) {
			        	// 如果方法上定义了@DynamicDataSource
			            DataSource data = m.getAnnotation(DataSource.class);
			            re=data.value();
			        } else  {
				        if (classz[0] != null && classz[0].isAnnotationPresent(DataSource.class)) {
				        	// 如果类定义了@DynamicDataSource
				            DataSource data = classz[0].getAnnotation(DataSource.class);
				            re=data.value();
				        }
				    }
		        }
		    } catch (Exception e) {
		    	 /*logger.error(String.format(" An error has occurred at %s when get dynamic dataSource:%s", 
			        		StackTraceUtils.getMethodFullName(), System.getProperty("line.separator"), e.toString()));*/
		    }
    	}
    	
    	String oldDs=DynamicDataSourceHolder.getDataSourceKey(); //v1.0.0.20180120, com.qzdatasoft.Koradji
    	if(!oldDs.isEmpty())oldDs=oldDs.substring(1);//v1.0.0.20180120, com.qzdatasoft.Koradji
    	
	    DynamicDataSourceHolder.setDataSourceKey(re);
	    Object rt = null;
        long ltime1 = System.currentTimeMillis();
        long execTime1 = 0;
	    try{
	    	rt=super.invoke(invocation);
	        execTime1 = System.currentTimeMillis() - ltime1;
	    }catch(BaseException e){
	    	throw e;
	    }catch(Exception e){
	    	e.printStackTrace();
	    	throw new Throwable();
	    }finally {
			//v1.0.0.20180120, com.qzdatasoft.Koradji start
	    	DynamicDataSourceHolder.setDataSourceKey(oldDs);
    		//DynamicDataSourceHolder.resetDataSourceKey();		//必须清空，不然下个非service中的方法调用时还是之前方法的数据源
			//v1.0.0.20180120, com.qzdatasoft.Koradji end
		}
        long execTime = System.currentTimeMillis() - ltime;
        
        if(logger.isDebugEnabled()){
        	String msg="=>"+this.getClass().getSimpleName()  + " Execution time = " + execTime + "ms but invoke() Execution time = " + execTime1 + "ms";
        	logger.debug(msg);
        }
        
        return rt;
	    
	  } 


}
