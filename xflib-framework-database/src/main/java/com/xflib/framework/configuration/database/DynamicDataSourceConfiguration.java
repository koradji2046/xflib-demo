/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration.database;

import java.io.IOException;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;

import com.xflib.framework.database.DynamicDataSource;
import com.xflib.framework.database.DynamicDataSourceProperties;
import com.xflib.framework.database.DynamicDatasourceTransactionInterceptor;

/**
 * @author koradji
 * @date 2019/1/27
 * 
 */
@Configuration
@EnableConfigurationProperties({ DynamicDataSourceProperties.class, 
    DataSourceProperties.class, JpaProperties.class })
@EnableTransactionManagement
public class DynamicDataSourceConfiguration {

    private JpaProperties jpaProperties;

    // private static final Logger logger =
    // LoggerFactory.getLogger(DynamicDataSourceConfiguration.class);

    public DynamicDataSourceConfiguration(DataSourceProperties properties, 
            JpaProperties jpaProperties,
            DynamicDataSourceProperties dynamicProperties) {
        this.jpaProperties=jpaProperties;
    }

    @Bean("datasource")
    public DynamicDataSource dynamicDataSource() {
        return new DynamicDataSource();
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean emf(
            EntityManagerFactoryBuilder builder, DataSource dataSource)
            throws IOException {
        return builder.dataSource(dataSource).properties(
                DynamicDataSource.getVendorProperties(jpaProperties, dataSource))
                // .packages("com.kxlist.statistics.domain.user") //设置实体类所在位置
                .persistenceUnit("userPersistenceUnit").build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            EntityManagerFactory emf, DynamicDataSource dataSource) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(dataSource);
        return tm;
    }

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

    @Bean
    public TransactionAttributeSourceAdvisor TransactionAttributeSourceAdvisorBean(
            DynamicDatasourceTransactionInterceptor dynamicTransactionInterceptor) {
        TransactionAttributeSourceAdvisor re = new TransactionAttributeSourceAdvisor();
        re.setTransactionInterceptor(dynamicTransactionInterceptor);
        return re;
    }

}
