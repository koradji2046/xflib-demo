/** Copyright (c) 2019 Koradji. */
package com.xflib.demo;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xflib.framework.common.ReturnMessageInfo;
import com.xflib.framework.utils.JsonUtils;

/**
 * @author koradji
 * @date 2019/1/27
 */
@SpringBootApplication
@ComponentScan(basePackages={
    "com.xflib.*.configuration",
    })
public class Startup {
	
	private static Logger log =LoggerFactory.getLogger(Startup.class);
	
    public static void main(String[] args) throws Exception {
        test();
    }

    private static void test(){
    	try {
			JsonTest();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void JsonTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException{
    	//Map
    	Map<String, ReturnMessageInfo> dataReturnMessageInfo=new HashMap<String, ReturnMessageInfo>();
    	dataReturnMessageInfo.put("a",new ReturnMessageInfo());
    	//Map2Str
    	String aStr=JsonUtils.toJson(dataReturnMessageInfo);
    	log.info(aStr);
    	//Str2Map
    	Map<String,ReturnMessageInfo> outReturnMessageInfo=JsonUtils.toMap(ReturnMessageInfo.class, aStr);
    	log.info(outReturnMessageInfo.get("a").getErrorMessage());
    
    }
}
