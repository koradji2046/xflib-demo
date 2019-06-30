/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.database.configure;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xflib.framework.database.DynamicDataSource;
import com.xflib.framework.database.DynamicDatasourceTransactionInterceptor;
import com.xflib.framework.database.TransactionInterceptorBeanPostProcessor;
import com.xflib.framework.database.utils.JedisUtils;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@EnableConfigurationProperties({ DynamicDataSourceProperties.class, 
    DataSourceProperties.class, JpaProperties.class,  IdGeneratorProperties.class })
//@ComponentScan(basePackageClasses=DynamicDatasourceAspect.class)
@EnableTransactionManagement
public class DynamicDataSourceConfiguration {

    private JpaProperties jpaProperties;

     private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);

    public DynamicDataSourceConfiguration(DataSourceProperties properties, 
            JpaProperties jpaProperties/*,
            DynamicDataSourceProperties dynamicProperties*/) {
        this.jpaProperties=jpaProperties;
    }

    @Bean("datasource")
    public DataSource dataSource() {
        return new DynamicDataSource();
    }
    
    @SuppressWarnings("deprecation")
	@Bean
    public JedisUtils jedisUtils(){
    	return new JedisUtils();
    }

    /**
     * 这个entityManagerFactory的使用条件：
     * 1 数据源类型相同
     * 2 数据源JPA属性相同
     * 3 除非(spring.jpa.generateDdl=false或者spring.jpa.hibernate.ddl-auto=none)
     *   表对象应该完全相同，否则会产生一些奇怪的错误
     *   注: spring.jpa.hibernate.ddl-auto可选值：create/update/create-drop/validate/none
     */
    @Bean(name = "entityManagerFactory")
    @ConditionalOnProperty(prefix="custom.datasource",name="persistenceUnitName",havingValue="primary",matchIfMissing=true)
    public LocalContainerEntityManagerFactoryBean emf(
            /*EntityManagerFactoryBuilder builder,  */
    		DataSource dataSource,
            EntityScanPackages entityScanPackages)
            throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    	
    	LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    	
    	factory.setDataSource(dataSource);
		factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		factory.setPackagesToScan(entityScanPackages.getPackageNames().toArray(new String[0]));
		factory.setJpaPropertyMap(jpaProperties.getProperties());
//		factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		factory.afterPropertiesSet();
		
		log.info("=>entityManagerFactory configed by default.");

		return factory;

//    	em.setDataSource(dataSource);
//    	em.setPackagesToScan(entityScanPackages.getPackageNames().toArray(new String[0]));
//    	em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//    	
//    	Properties projerties=new Properties();
//    	for(Map.Entry<String,String> e:jpaProperties.getHibernate()..entrySet() ){
//    		projerties.put(e.getKey(), e.getValue());
//    	};
//    	em.setJpaProperties(projerties);
//        return builder.dataSource(dataSource).properties(
//                DynamicDataSource.getVendorProperties(jpaProperties, dataSource))
//                // .packages("com.kxlist.statistics.domain.user") //设置实体类所在位置
//                .persistenceUnit("userPersistenceUnit")
//                .build();
//    	return em;
    }

//    @Bean(name = "transactionManager")
//    public PlatformTransactionManager transactionManager(
//            EntityManagerFactory emf, DynamicDataSource dataSource) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(emf);
//        tm.setDataSource(dataSource);
//        return tm;
//    }

    @Bean(name = "dynamicTransactionInterceptor")
    public DynamicDatasourceTransactionInterceptor dynamicDatasourceTransactionInterceptor(
            PlatformTransactionManager transactionManager) {
        DynamicDatasourceTransactionInterceptor re =
                new DynamicDatasourceTransactionInterceptor();
        re.setTransactionManager(transactionManager);
        AnnotationTransactionAttributeSource x = new AnnotationTransactionAttributeSource();
        re.setTransactionAttributeSource(x);
        return re;
    }

//    @Bean
//    public TransactionAttributeSourceAdvisor TransactionAttributeSourceAdvisorBean(
//            DynamicDatasourceTransactionInterceptor dynamicTransactionInterceptor) {
//        TransactionAttributeSourceAdvisor re = new TransactionAttributeSourceAdvisor();
//        re.setTransactionInterceptor(dynamicTransactionInterceptor);
//        return re;
//    }
    
    @Bean
    public TransactionInterceptorBeanPostProcessor transactionInterceptorBeanPostProcessor(
    			DynamicDatasourceTransactionInterceptor dynamicDatasourceTransactionInterceptor){
    	return new TransactionInterceptorBeanPostProcessor(dynamicDatasourceTransactionInterceptor);
    }

}
