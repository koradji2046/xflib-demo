package com.xflib.framework.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.xflib.framework.redis.RedisDriver;
import com.xflib.framework.redis.RedisImportSelector;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(RedisImportSelector.class)
public @interface EnableRedis {
	/**
	 * REDIS引擎类型
	 * @return
	 */
	RedisDriver driver() default RedisDriver.JEDIS;
	
	/**
	 * 是否支持动态数据源
	 * @return
	 */
	boolean isDynamic() default false;
}
