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

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRedisConnectionFactory implements RedisConnectionFactory {

    @Autowired
    private DynamicRedisProperties dynamicRedisProperties;

    @Autowired
    private RedisProperties defaultRedisProperties;

    private Map<String,RedisConnectionFactory> jedisConnectionFactorys= new HashMap<>();

    @PostConstruct
    public void createRedisConnectionFactory() throws UnknownHostException {
        dynamicRedisProperties.getList().forEach((redisPropertiesEx)->{
          String site=redisPropertiesEx.getSite();
          RedisProperties config = redisPropertiesEx.getConfig();
          createJedisConnectionFactory(site, config);
       });
        if (!jedisConnectionFactorys.containsKey("default")){
            createJedisConnectionFactory("default", defaultRedisProperties);
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

    protected RedisConnectionFactory determineRedisConnectionFactory() /*throws BaseException*/{
        RedisConnectionFactory jedisConnectionFactory=null;
        String site=DynamicRedisHolder.getSite();
        String key=(site==null||site.isEmpty()?"default":site);
        if(jedisConnectionFactorys.containsKey(key)){
            jedisConnectionFactory= jedisConnectionFactorys.get(key);
        }
        return jedisConnectionFactory;//DynamicRedisHolder.getRedisConnectionFactoryContext();
    }

    private void createJedisConnectionFactory(String site,RedisProperties config){
        RedisConnectionFactory jedisConnectionFactory=
                (new MetaJedisConnectionFactory())
                .createRedisConnectionFactory(config);
        jedisConnectionFactorys.put(site, jedisConnectionFactory);
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
