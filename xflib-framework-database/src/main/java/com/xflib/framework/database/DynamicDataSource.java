/**Copyright: Copyright (c) 2016, Koradji */
package com.xflib.framework.database;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.xflib.framework.database.configure.DynamicDataSourceProperties;
import com.xflib.framework.database.utils.DynamicDataSourceHolder;

/**
 * Dao动态数据源路由器<br>
 * 
 * History:<br>
 * . 1.0.0.20161024, Koradji, Create<br>
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	static DynamicDataSource instance;
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

    private Map<Object, Object> targetDataSources=new HashMap<>();

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Autowired
    private DataSourceProperties defaultDataSourceProperties;
    
    @Autowired
    private ApplicationContext context;

//    private boolean defaultHasDefined = false;

//    public static Map<String, String> getVendorProperties(JpaProperties jpaProperties, DataSource dataSource) {
//        return jpaProperties.getHibernateProperties(dataSource);
//    }

    @PostConstruct
    public void init() {
        
    	instance=this;
    	/*
         * DataSource创建逻辑 1
         * 每个站点必须有一个master/default数据源，其中master和default既是主数据源也是默认数据源 2
         * master和default数据源只创建一个实例，master/default谁先定义谁优先，另外一个定义会被忽略 3
         * master/slave数据源是为读写分离定义的，如果使用读写分离，必须定义slave数据源 4
         * 默认的写数据源是master/default，如果读数据源slave不存在，读数据源也是master/default 5
         * 如果未定义master，将会以spring.datasource的设置定义default数据源 6 
         * 数据源的命名格式为: dataSource-[site]-[source] 7
         * 站点default若果不在custom.datasource中定义，会使用spring.datasource的设置定义DataSource-default-master/default数据源 8
         * 系统初始化后，defaultTargetDataSource=DataSource-default-default 9
         * 注意：必须通过ScanEntity注解指定basePackages来注册entityBean，否则全文搜索Entity注解会影响其他独立EntityManager的注册 A
         * 站点读写分离数据源的JpaProperties使用spring.jpa设置 B
         * source!=defualt/maste/slave的会被定义为独立数据源，使用单独的emf和tm, 只能显式指定别名使用 C
         *  - emf(EntityManagerFactory)名称为:  emf-[site]-[source]
         *  - tm(TransactionManager)名称为: tm-[site]-[source]
         *  - EntityManager数据源须自行指定packages及jpa设置，例如：
         * @Bean(name = "entityManagerFactoryA")
         * public LocalContainerEntityManagerFactoryBean emfA(
         *   EntityManagerFactoryBuilder builder
         * , DataSource dataSource) throws IOException {
         *      return builder
         *               .dataSource(dataSource)
         *               .properties(getVendorProperties(dataSource))
         *               .packages("com.kxlist.statistics.domain.user")
         *               .persistenceUnit("userPersistenceUnit")
         *               .build();
         * }
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
            log.debug("=> Datasource [{}] has registed.", beanName);
        } catch (Exception e) {
        	if(log.isDebugEnabled()){
        		e.printStackTrace();
        	}else{
            	log.error(e.getMessage());
        	}
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

    static class MetaDataSourceFactory {

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
            beanDefinitionBuilderUtils.registerBean(beanName, DruidDataSource.class, (beanDefinitionBuilder) -> {
                ps.forEach((key, value) -> {
                    beanDefinitionBuilder.addPropertyValue(key, value);
                });
                return true;
            });
        }

        public static void registerEMF(String beanName, String dataSource, JpaProperties jpa, List<String> packages)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Map<String, String> ps = BeanUtils.describe(jpa);
            beanDefinitionBuilderUtils.registerBean(beanName, LocalContainerEntityManagerFactoryBean.class,
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
            beanDefinitionBuilderUtils.registerBean(transactionManagerId, JpaTransactionManager.class,
                    (beanDefinitionBuilder) -> {
                        beanDefinitionBuilder.addPropertyReference("dataSource", dataSourceId);
                        beanDefinitionBuilder.addPropertyReference("entityManagerFactory",  entityManagerFactoryId);
                        return true;
                    });
        }

    }

    static class beanDefinitionBuilderUtils {
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
    		ConfigurableApplicationContext configurableContext = (ConfigurableApplicationContext) DynamicDataSource.instance.context;
    		BeanDefinitionRegistry beanDefinitionRegistry = (DefaultListableBeanFactory) configurableContext
    				.getBeanFactory();
    		return beanDefinitionRegistry;
    	}
   	
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
   }
    
//    @Deprecated
//    static class JedisUtil {
//    	
//    	@SuppressWarnings("rawtypes")
//		public static <T>DefaultRedisScript redisScript2(Class<T> type, String script){
//    		DefaultRedisScript<T> redisScript=new DefaultRedisScript<>();
//    		redisScript.setScriptText(script);
//    		redisScript.setResultType(type);
//    		return redisScript;
//    	}
//
//    	@SuppressWarnings("rawtypes")
//    	public static RedisTemplate redisTemplate(){
//    		RedisTemplate redisTemplate=(RedisTemplate) DynamicDataSource.instance.context.getBean("jsonRedisTemplate");
//    		return redisTemplate;
//    	}
//    	
//    	/**
//    	 * 缓存是否存在<br>
//    	 * 
//    	 * @param key  缓存key值
//    	 * @return
//    	 */
//    	@SuppressWarnings("unchecked")
//		public static boolean exists(String key,String hashKey){
//    		HashOperations<String,String,Long> ops = redisTemplate().opsForHash();
//    		return ops.hasKey(key, hashKey);
//    	}
//    	
//    	
//    	/**
//    	 * 设置缓存<br>
//    	 * 
//    	 * @param key  缓存key值
//    	 * @param value  缓存值(数据)
//    	 * @return
//    	 */
//    	@SuppressWarnings("unchecked")
//    	public static boolean set(String key,String hashKey,Long value){
//    		boolean exist = false;
//    		try{
//    			HashOperations<String,String,Long> ops = redisTemplate().opsForHash();
//    			ops.put(key, hashKey, value);
//    			exist=true;
//    		}finally{
//    		}
//    		return exist;
//    	} 
//    	
//    	
//    	/**
//    	 * 缓存是否存在<br>
//    	 * 
//    	 * @param key  缓存key值
//    	 * @return
//    	 */
//    	@SuppressWarnings("unchecked")
//    	public static Long incr(String key,String hashKey){
//    		HashOperations<String,String,Long> ops = redisTemplate().opsForHash();
//    		return ops.increment(key,hashKey, 1L);
//    	}
//
//    }

}