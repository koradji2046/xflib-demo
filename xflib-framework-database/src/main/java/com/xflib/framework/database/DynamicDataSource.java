/**Copyright: Copyright (c) 2016, Koradji */
package com.xflib.framework.database;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.xflib.framework.utils.SpringUtils;

/**
 * Dao动态数据源路由器<br>
 * 
 * History:<br>
 * . 1.0.0.20161024, Koradji, Create<br>
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

    private Map<Object, Object> targetDataSources=new HashMap<>();

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Autowired
    private DataSourceProperties defaultDataSourceProperties;

//    private boolean defaultHasDefined = false;

//    public static Map<String, String> getVendorProperties(JpaProperties jpaProperties, DataSource dataSource) {
//        return jpaProperties.getHibernateProperties(dataSource);
//    }

    @PostConstruct
    public void init() {
        /*
         * DataSource创建逻辑 1
         * 每个站点必须有一个master/default数据源，其中master和default及时主数据源也是默认数据源 2
         * master和default数据源只创建一个实例，master/default谁先定义谁优先，另外一个定义会被忽略 3
         * master/slave数据源是为读写分离定义的，如果使用读写分离，必须定义slave数据源 4
         * 默认的写数据源是master/default，如果读数据源slave不存在，读数据源也是master/default 5
         * 如果未定义master，将会以spring.datasource的设置定义default数据源 6 数据源的命名格式为:
         * dataSource-[site]-[source] 7
         * 站点default若果不在custom.datasource中定义，会使用spring.datasource的设置定义DataSource
         * -default-master/default数据源 8
         * 系统初始化后，defaultTargetDataSource=DataSource-default-default 9
         * 注意：必须通过ScanEntity注解指定basePackages来注册entity
         * Bean，否则全文搜索Entity注解会影响其他独立EntityManager的注册 A
         * 站点读写分离数据源的JpaProperties使用spring.jpa设置 B
         * source!=master/defualt/slave的会被定义为独立数据源，使用单独的emf(名称为[source]
         * EntityManagerFactory), 只能显式指定别名使用 C
         * 创建其他独立EntityManager数据源须自行指定packages及jpa设置，例如：
         * 
         * @Bean(name = "entityManagerFactoryA") public
         * LocalContainerEntityManagerFactoryBean emfA(
         * EntityManagerFactoryBuilder builder, DataSource dataSource) throws
         * IOException { return
         * builder.dataSource(dataSource).properties(getVendorProperties(
         * dataSource)) .packages("com.kxlist.statistics.domain.user")
         * //设置实体类所在位置 .persistenceUnit("userPersistenceUnit").build();
         */
        dynamicDataSourceProperties.getList().forEach((siteSourceDatasourceProperties) -> {// 创建站点指定datasource
            String site = siteSourceDatasourceProperties.getSite();
            boolean defaultHasDefined = false;
            for(SiteSourceDataSourceProperties siteSourceRedisPrperties : siteSourceDatasourceProperties.getSources()){
//            siteSourceDatasourceProperties.getSources().forEach((siteSourceRedisPrperties) -> {
                boolean isNotDefined = true;
                String source = siteSourceRedisPrperties.getSource();
                if (source.equals("master") || source.equals("default")) {
                    isNotDefined = !defaultHasDefined;
                }
                if (isNotDefined) {
                    DataSourceProperties config = siteSourceRedisPrperties.getConfig();
                    if (source.equals("master") || source.equals("default") || source.equals("slave")) {
                        addTargetDatasource(site, source, config);
                    } else {// 定义独立数据源
                        JpaProperties jpa = siteSourceRedisPrperties.getJpa();
                        List<String> packages = siteSourceRedisPrperties.getPackages();
                        registerDataSource(site, source, config, jpa, packages);
                    }
                    if (source.equals("master") || source.equals("default")) {
                        defaultHasDefined = true;
                    }
                }
            };
            //);
        });
        String beanName = String.format("dataSource-%s-%s", "default", "default");
        if (!targetDataSources.containsKey(beanName)) {// //创建默认站点datasource
            addTargetDatasource("default", "default", defaultDataSourceProperties);
        }

        // 设置默认datasource
        this.setDefaultTargetDataSource(this.targetDataSources.get(beanName));
        this.setTargetDataSources(targetDataSources);
        this.afterPropertiesSet();

    }

    // 定义读写分离数据源
    private void addTargetDatasource(String site, String source, DataSourceProperties config) {
        DataSource dataSource;
        try {
            dataSource = MetaDataSourceFactory.createDataSource(config);
            String beanName = String.format("dataSource-%s-default", site, source);
            if (source.equals("master") || source.equals("default")) {
                beanName = String.format("dataSource-%s-default", site);
                targetDataSources.put(beanName, dataSource);
                beanName = String.format("dataSource-%s-master", site);
                targetDataSources.put(beanName, dataSource);
            } else {
                targetDataSources.put(beanName, dataSource);
            }
            if (log.isDebugEnabled()) {
                log.debug("=> Datasource [{}] has registed.", beanName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定义独立数据源
    private void registerDataSource(String site, String source, DataSourceProperties config, JpaProperties jpa,
            List<String> packages) {

        try {
            // Register datasource
            String datasourceName = String.format("ds-%s-%s", site, source);
            MetaDataSourceFactory.registerDataSource(datasourceName, config);

            // Register entityManagerFactory, to use stand alone jpa properties & entity component packages
            String emfName = String.format("emf-%s-%s", site, source);
            MetaDataSourceFactory.registerEMF(emfName, datasourceName, jpa, packages);

            // Register transactionManager
            String tmName = String.format("tm-%s-%s", site, source);
            MetaDataSourceFactory.registerTM(tmName, datasourceName, emfName);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static class MetaDataSourceFactory {

        private static Map<String, String> getVendorProperties(DataSourceProperties properties)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Map<String, String> map = BeanUtils.describe(properties);
            return map;
        }

        public static DataSource createDataSource(DataSourceProperties properties)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, Exception {
            DataSource datasource = DruidDataSourceFactory.createDataSource(getVendorProperties(properties));
            return datasource;
        }

        public static void registerDataSource(String beanName, DataSourceProperties properties)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Map<String, String> ps = getVendorProperties(properties);
            SpringUtils.registerBean(beanName, DruidDataSource.class, (beanDefinitionBuilder) -> {
                ps.forEach((key, value) -> {
                    beanDefinitionBuilder.addPropertyValue(key, value);
                });
                return true;
            });
        }

        public static void registerEMF(String beanName, String dataSource, JpaProperties jpa, List<String> packages)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Map<String, String> ps = BeanUtils.describe(jpa);
            SpringUtils.registerBean(beanName, LocalContainerEntityManagerFactoryBean.class,
                    (beanDefinitionBuilder) -> {
                        beanDefinitionBuilder.addPropertyReference("dataSource", dataSource);
                        beanDefinitionBuilder.addPropertyValue("persistenceUnitInfo", "userPersistenceUnit");
                        beanDefinitionBuilder.addPropertyValue("packagesToScan", packages.toArray());
                        ps.forEach((key, value) -> {
                            beanDefinitionBuilder.addPropertyValue(key, value);
                        });
                        return true;
                    });
        }

        public static void registerTM(String transactionManagerId, String dataSourceId, String entityManagerFactoryId)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            SpringUtils.registerBean(transactionManagerId, JpaTransactionManager.class,
                    (beanDefinitionBuilder) -> {
                        beanDefinitionBuilder.addPropertyReference("dataSource", dataSourceId);
                        beanDefinitionBuilder.addPropertyReference("entityManagerFactory",  entityManagerFactoryId);
                        return true;
                    });
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#
     * determineCurrentLookupKey()
     */
    @Override
    protected Object determineCurrentLookupKey() {
        @SuppressWarnings("deprecation")
        String key = DynamicDataSourceHolder.getDataSource();
        if(!this.targetDataSources.containsKey(key)){
        	throw new RuntimeException("=> 指定的数据源不存在!");
        }
        if (logger.isDebugEnabled()) {
            logger.info("=> 当前使用的数据源:" + key);
        }
        return key;
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
    }

//    public Map<Object, Object> dataSources() {
//        return this.targetDataSources;
//    }

}