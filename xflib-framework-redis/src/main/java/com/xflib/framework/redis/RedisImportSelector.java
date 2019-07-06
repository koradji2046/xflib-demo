package com.xflib.framework.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import com.xflib.framework.redis.annotation.EnableRedis;
import com.xflib.framework.redis.configure.DynamicRedisHolderConfiguration;
import com.xflib.framework.redis.configure.JsonRedisTemplateConfiguration;
import com.xflib.framework.redis.jedis.configure.DynamicRedisAutoConfiguration;
import com.xflib.framework.redis.redisson.configure.DynamicRedissonAutoConfiguration;
import com.xflib.framework.redis.redisson.configure.RedissonAutoConfiguration;

public class RedisImportSelector implements ImportSelector {

	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		
		String result=RedisAutoConfiguration.class.getName();
		// 获取EnableConfigurationProperties注解的所有的属性值
		MultiValueMap<String, Object> attributes = 
				importingClassMetadata.getAllAnnotationAttributes(	EnableRedis.class.getName(), false);
             // 获取attributes中主键value的第一个值。
//        String[] type = attributes == null ? null  : (String[]) attributes.getFirst("driver");
        RedisDriver driver=(RedisDriver)attributes.getFirst("driver");
//        String driver=type[0];
        boolean isDynamic=(boolean) attributes.getFirst("isDynamic");
        if(RedisDriver.JEDIS.equals(driver)){
        	if(isDynamic) {
        		result=DynamicRedisAutoConfiguration.class.getName();
        	}
        }else if(RedisDriver.REDISSION.equals(driver)){
        	if(isDynamic) {
        		result=DynamicRedissonAutoConfiguration.class.getName();
        	}else{
        		result=RedissonAutoConfiguration.class.getName();
        	}
        }      
 		
		return  new String[]{DynamicRedisHolderConfiguration.class.getName(),result,
		JsonRedisTemplateConfiguration.class.getName()};
	}

//	@Override
//	protected boolean isEnabled(AnnotationMetadata metadata) {
//		if (getClass().equals(EnableAutoConfigurationImportSelector.class)) {
//			return getEnvironment().getProperty(
//					EnableAutoConfiguration.ENABLED_OVERRIDE_PROPERTY, Boolean.class,
//					true);
//		}
//		return true;
//	}

}
