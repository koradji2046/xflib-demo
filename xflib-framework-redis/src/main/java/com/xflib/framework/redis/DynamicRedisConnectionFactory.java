/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.redis;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;

import com.xflib.framework.common.BaseException;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class DynamicRedisConnectionFactory implements RedisConnectionFactory {

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

    protected RedisConnectionFactory determineRedisConnectionFactory() throws BaseException{
        return DynamicRedisHolder.getRedisConnectionFactoryContext();
    }
}
