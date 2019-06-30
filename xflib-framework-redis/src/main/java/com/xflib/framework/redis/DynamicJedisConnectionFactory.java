/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.xflib.framework.autoconfig.redis.DynamicRedisProperties;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRedisConnectionFactory implements RedisConnectionFactory {

    private Logger log = LoggerFactory.getLogger(DynamicRedisConnectionFactory.class);
    private Map<String,RedisConnectionFactory> jedisConnectionFactorys= new HashMap<>();

    @Autowired
    private DynamicRedisProperties dynamicRedisProperties;

    @Autowired
    private RedisProperties defaultRedisProperties;

    @PostConstruct
    public void createRedisConnectionFactory() throws UnknownHostException {
        dynamicRedisProperties.getList().forEach((siteRedisProperties)->{//创建站点指定redis数据源
          String site=siteRedisProperties.getSite();
          siteRedisProperties.getSources().forEach((siteSourceRedisPrperties)->{
              String source=siteSourceRedisPrperties.getSource();
              String beanName=String.format("redis-%s-%s", site,source);
              RedisProperties config=siteSourceRedisPrperties.getConfig();
              createJedisConnectionFactory(beanName, config);
              });
        });
        dynamicRedisProperties.getSites().forEach((site)->{//创建站点默认redis数据源
            String beanName=String.format("redis-%s-%s", site,"default");
            if (!jedisConnectionFactorys.containsKey(beanName)){
                createJedisConnectionFactory(beanName, defaultRedisProperties);
            }
        });
        String beanName=String.format("redis-%s-%s", "default","default");
        if (!jedisConnectionFactorys.containsKey(beanName)){// //创建默认站点redis数据源
            createJedisConnectionFactory(beanName, defaultRedisProperties);
        }
    }
    
    @Override
    public RedisConnection getConnection() {
        return determineRedisConnectionFactory().getConnection();
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        return determineRedisConnectionFactory().getClusterConnection();
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        return determineRedisConnectionFactory().getSentinelConnection();
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return determineRedisConnectionFactory().getConvertPipelineAndTxResults();
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return determineRedisConnectionFactory().translateExceptionIfPossible(ex);
    }

    protected RedisConnectionFactory determineRedisConnectionFactory() {
        RedisConnectionFactory jedisConnectionFactory=null;
        String site=DynamicRedisHolder.getSite();
        site=(site==null||site.isEmpty()?"default":site);
        String source=DynamicRedisHolder.getSource();
        source=(source==null||source.isEmpty()?"default":source);
        String beanName=String.format("redis-%s-%s", site,source);
        if(jedisConnectionFactorys.containsKey(beanName)){
            jedisConnectionFactory= jedisConnectionFactorys.get(beanName);
            if(log.isDebugEnabled()){
                log.debug("=> Current redis datasource is [{}]", beanName);
            }
        }else{
            if(log.isErrorEnabled()){
                log.error("=> redis datasource [{}] is not defined.", beanName);
            }
        }
        return jedisConnectionFactory;
    }

    private void createJedisConnectionFactory(String beanName/*String site,String source*/,RedisProperties config){
        RedisConnectionFactory jedisConnectionFactory=
                (new MetaJedisConnectionFactory())
                .createRedisConnectionFactory(config);
//        String beanName=String.format("redis-%s-%s", site,source);
        jedisConnectionFactorys.put(beanName, jedisConnectionFactory);
        if(log.isDebugEnabled()){
            log.debug("=> Redis datasource [{}] has registed.",beanName);
        }
    }

    private class MetaJedisConnectionFactory{
        
        private RedisProperties properties;
        public JedisConnectionFactory createRedisConnectionFactory(RedisProperties properties)/* throws UnknownHostException*/ {
            this.properties=properties;
            JedisConnectionFactory jedisConnectionFactory = applyProperties(createJedisConnectionFactory());
            jedisConnectionFactory.afterPropertiesSet();
            return jedisConnectionFactory;
        }
        
        private final JedisConnectionFactory applyProperties(JedisConnectionFactory factory) {
            configureConnection(factory);
            if (this.properties.isSsl()) {
                factory.setUseSsl(true);
            }
            factory.setDatabase(this.properties.getDatabase());
            if (this.properties.getTimeout() > 0) {
                factory.setTimeout(this.properties.getTimeout());
            }
            return factory;
        }

        private void configureConnection(JedisConnectionFactory factory) {
            if (StringUtils.hasText(this.properties.getUrl())) {
                configureConnectionFromUrl(factory);
            } else {
                factory.setHostName(this.properties.getHost());
                factory.setPort(this.properties.getPort());
                if (this.properties.getPassword() != null) {
                    factory.setPassword(this.properties.getPassword());
                }
            }
        }

        private void configureConnectionFromUrl(JedisConnectionFactory factory) {
            String url = this.properties.getUrl();
            if (url.startsWith("rediss://")) {
                factory.setUseSsl(true);
            }
            try {
                URI uri = new URI(url);
                factory.setHostName(uri.getHost());
                factory.setPort(uri.getPort());
                if (uri.getUserInfo() != null) {
                    String password = uri.getUserInfo();
                    int index = password.indexOf(":");
                    if (index >= 0) {
                        password = password.substring(index + 1);
                    }
                    factory.setPassword(password);
                }
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url, ex);
            }
        }

        private final RedisSentinelConfiguration getSentinelConfig() {
            Sentinel sentinelProperties = this.properties.getSentinel();
            if (sentinelProperties != null) {
                RedisSentinelConfiguration config = new RedisSentinelConfiguration();
                config.master(sentinelProperties.getMaster());
                config.setSentinels(createSentinels(sentinelProperties));
                return config;
            }
            return null;
        }

        /**
         * Create a {@link RedisClusterConfiguration} if necessary.
         * 
         * @return {@literal null} if no cluster settings are set.
         */
        private final RedisClusterConfiguration getClusterConfiguration() {
            if (this.properties.getCluster() == null) {
                return null;
            }
            Cluster clusterProperties = this.properties.getCluster();
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());

            if (clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }
            return config;
        }

        private List<RedisNode> createSentinels(Sentinel sentinel) {
            List<RedisNode> nodes = new ArrayList<RedisNode>();
            for (String node : StringUtils.commaDelimitedListToStringArray(sentinel.getNodes())) {
                try {
                    String[] parts = StringUtils.split(node, ":");
                    Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                    nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
                } catch (RuntimeException ex) {
                    throw new IllegalStateException("Invalid redis sentinel " + "property '" + node + "'", ex);
                }
            }
            return nodes;
        }

        private JedisConnectionFactory createJedisConnectionFactory() {
            JedisPoolConfig poolConfig = this.properties.getPool() != null ? jedisPoolConfig() : new JedisPoolConfig();

            if (getSentinelConfig() != null) {
                return new JedisConnectionFactory(getSentinelConfig(), poolConfig);
            }
            if (getClusterConfiguration() != null) {
                return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
            }
            return new JedisConnectionFactory(poolConfig);
        }

        private JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig config = new JedisPoolConfig();
            RedisProperties.Pool props = this.properties.getPool();
            config.setMaxTotal(props.getMaxActive());
            config.setMaxIdle(props.getMaxIdle());
            config.setMinIdle(props.getMinIdle());
            config.setMaxWaitMillis(props.getMaxWait());
            return config;
        }
    }

}
