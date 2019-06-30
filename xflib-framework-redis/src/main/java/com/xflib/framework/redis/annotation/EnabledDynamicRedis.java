package com.xflib.framework.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.xflib.framework.redis.configure.DynamicRedisConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DynamicRedisConfiguration.class)
public @interface EnabledDynamicRedis {
//	String[] value() default "Jedis";     // 占位，未来支持Redisson
}
