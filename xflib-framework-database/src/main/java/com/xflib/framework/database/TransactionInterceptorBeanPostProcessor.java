package com.xflib.framework.database;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;

/**
 * 自定义的后置处理器
 * Created by smlz on 2019/4/3.
 */
public class TransactionInterceptorBeanPostProcessor implements BeanPostProcessor{

	private DynamicDatasourceTransactionInterceptor dynamicTransactionInterceptor;
	
	public TransactionInterceptorBeanPostProcessor(DynamicDatasourceTransactionInterceptor dynamicTransactionInterceptor){
		this.dynamicTransactionInterceptor=dynamicTransactionInterceptor;
	}
	
	@Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
    }

	@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof TransactionAttributeSourceAdvisor) {
        	TransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor=(TransactionAttributeSourceAdvisor)bean;
        	((TransactionAttributeSourceAdvisor) bean).setTransactionInterceptor(dynamicTransactionInterceptor);
            return transactionAttributeSourceAdvisor;
        }
        return bean;
    }

}
