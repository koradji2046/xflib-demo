package com.xflib.framework.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import net.sf.json.JsonConfig;

/**
 * JSON转换工具 <br>
 * <br>
 * History:<br>
 * . v1.0.0.20170105, WangShi, Create<br>
 * . v1.0.0.20170518, WangShi, 增加JSON字符串转List方法，<br>
 */
public class JsonUtils {
	
	public static String DEFAULT_DATAFORMAT= "yyyy-MM-dd HH:mm:ss fff";

	/**
	 * 对象转换成JSON字符串, 不支持Map/List内嵌对象为非基本类型的自动转换
	 * @param obj
	 * @param dateFormatString 默认格式为"yyyy-MM-dd HH:mm:ss fff"
	 * @return
	 * @throws JsonProcessingException
	 */
	public static <T> String toJson(T obj, String dateFormatString) 
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(dateFormatString));
		return mapper.writeValueAsString(obj);
	}
	public static <T> String toJson(T obj) throws JsonProcessingException {
		return toJson(obj, DEFAULT_DATAFORMAT);
	}
	
	/**
	 * JSON字符串转换成对象, 不支持Map/List内嵌对象为非基本类型的自动转换
	 * 
	 * @param type
	 * @param jsonStr
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T toObject(Class<T> type,String jsonStr, String dateFormatString) 
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(dateFormatString));
	    return (T) mapper.readValue(jsonStr, type);
	}
	public static <T> T toObject(Class<T> type,String jsonStr) 
			throws JsonParseException, JsonMappingException, IOException {
	    return toObject(type, jsonStr, DEFAULT_DATAFORMAT);
	}
	
	/**
	 * JSON字符串转换为Map<String,T>
	 * 
	 * @param type
	 * @param jsonStr
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> toMap(Class<T> type,String jsonStr, String dateFormatString)  
			throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Map<String,T> result=new LinkedHashMap<String,T>();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(dateFormatString));
		Map<String, Object> o=mapper.readValue(jsonStr, Map.class);
		
		for(Map.Entry<String, Object> e : o.entrySet()){
			T a=type.newInstance();
			BeanUtils.populate(a, (Map<String, Object>) e.getValue());
			result.put(e.getKey(),a);
		}
		
		return result;
	}
	public static <T> Map<String,T> toMap(Class<T> type,String jsonStr)  
			throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return toMap(type, jsonStr, DEFAULT_DATAFORMAT);
	}	
	/**
	 * JSON字符串转换为List<T>
	 * 
	 * @param type
	 * @param jsonStr
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> toList(Class<T> type,String jsonStr, String dateFormatString)  
			throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
		List<T> result=new ArrayList<T>();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat(dateFormatString));
		List<Object> o=mapper.readValue(jsonStr, List.class);
		
		for(int index=0;index< o.size(); index++){
			T a=type.newInstance();
			BeanUtils.populate(a, (Map<String, Object>) o.get(index));
			result.add(a);
		}
		
		return result;
	}
	public static <T> List<T> toList(Class<T> type,String jsonStr)  
			throws JsonParseException, JsonMappingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return toList(type, jsonStr, DEFAULT_DATAFORMAT);
	}	


}
