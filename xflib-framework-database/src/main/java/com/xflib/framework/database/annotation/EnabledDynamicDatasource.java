package com.xflib.framework.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.xflib.framework.database.configure.DynamicDataSourceConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(DynamicDataSourceConfiguration.class)
public @interface EnabledDynamicDatasource {
//	String[] value() default "MySql";     // 占位，未来支持多种数据库
}
