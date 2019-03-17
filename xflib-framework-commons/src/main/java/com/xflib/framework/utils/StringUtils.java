
/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/

package com.xflib.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 字符串操作工具<br>
 * <br>
 * History:<br>
 * . v1.0.0.20170527, wangshi, Create<br>
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
	protected static final Log log = LogFactory.getLog(StringUtils.class);

	/**
	 * 把源对象转换为String,如果失败将返回默认值
	 * 
	 * @param val		输入值
	 * @param defVal	默认值
	 * @return String
	 */
	public static String toString(Object val, String defVal) {
		try {
			return defaultIfBlank(val.toString(), defVal);
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 把源对象转换为String,如果失败将返回 EMPTY
	 * 
	 * @param val	输入值
	 * @return String
	 */
	public static String toString(Object val) {
		return toString(val, EMPTY);
	}

	/**
	 * 把源对象转换为double,如果失败将返回默认值
	 * 
	 * @param val		输入值
	 * @param defVal	默认值
	 * @return	Double
	 */
	public static Double toDouble(Object val, Double defVal) {
		try {
			return Double.valueOf(val.toString());
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 把源对象转换为double,如果失败将返回0
	 * 
	 * @param val	输入值
	 * @return	Double
	 */
	public static Double toDouble(Object val) {
		return toDouble(val, 0D);
	}

	/**
	 * 把源对象转换为long,如果失败将返回默认值
	 * 
	 * @param val		输入值
	 * @param defVal	默认值
	 * @return Long
	 */
	public static Long toLong(Object val, Long defVal) {
		try {
			return Long.valueOf(val.toString());
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 把源对象转换为long,如果失败将返回0
	 * 
	 * @param val	输入值
	 * @return	Long
	 */
	public static Long toLong(Object val) {
		return toLong(val, 0L);
	}

	/**
	 * 把源对象转换为int,如果失败将返回默认值
	 * 
	 * @param val		输入值
	 * @param defVal	默认值
	 * @return Integer
	 */
	public static Integer toInt(Object val, Integer defVal) {
		try {
			return Integer.valueOf(val.toString());
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 把源对象转换为int,如果失败将返回0
	 * 
	 * @param val	输入值
	 * @return	Integer
	 */
	public static Integer toInt(Object val) {
		return toInt(val, 0);
	}

	/**
	 * 把源对象转换为boolean,如果失败将返回默认值
	 * 
	 * @param val	输入值
	 * @param defVal	默认值
	 * @return Boolean
	 */
	public static Boolean toBoolean(Object val, Boolean defVal) {
		try {
			return Boolean.valueOf(val.toString());
		} catch (Exception e) {
			return defVal;
		}
	}

	/**
	 * 把源对象转换为boolean,如果失败将返回false
	 * 
	 * @param val		输入值
	 * @return	Boolean
	 */
	public static Boolean toBoolean(Object val) {
		return toBoolean(val, false);
	}
	
	
	/**
	 * 将字符串转换为16进制字符传人
	 * 
	 * @param str	输入字符串
	 * @return	转换后字符串
	 */
	public static String toHexString(String str){
		StringBuilder newStr = new StringBuilder();
		for(int i=0;i<str.length();i++){
			int ch = (int)str.charAt(i);
			String s4 = Integer.toHexString(ch);
			newStr.append(s4);
		}
		return newStr.toString();
	}
	
	
	/**
	 * 将符号隔开的字符串转换为何字符串列表
	 * 
	 * @param str		字符串
	 * @param symbol	分隔符
	 * @return	字符串列表
	 */
	public static List<String> formatStrArry(String str,String symbol){
		if(StringUtils.isNotBlank(str)){
			List<String> codes = new ArrayList<>();
			for (String code : str.split(symbol)) {
				codes.add(code);
			}
			return codes;
		}
		return null;
	}
	
	
	/**
	 * 将字符串列表转换为以 符号隔开的字符串
	 * 
	 * @param strs		列表
	 * @param symbol	分隔符
	 * @return	字符串列表
	 */
	public static String formatListToStr(List<String> strs,String symbol){
		
		String arrayStr = "";
		if(strs!=null && strs.size()>0){
			for(String str:strs){
				if("".equals(arrayStr)){
					arrayStr = str;
				}else{
					arrayStr = arrayStr + symbol +str;
				}
			}
		}
		return arrayStr;
	}
	
	
	/**
	 * 将大字段类型转换为字符串
	 * 
	 * @param clob		数据对象
	 * @return			字符串
	 * @throws SQLException  SQLException
	 * @throws IOException   IOException
	 */
	public static String clobToString(Clob clob) throws SQLException,IOException{
		String reString = "";
		BufferedReader br = null;
		try{
			Reader is = clob.getCharacterStream();
			br = new BufferedReader(is);
			String s = br.readLine();
			StringBuffer sb = new StringBuffer();
			while(s!=null){
				sb.append(s).append("\n");
				s = br.readLine();
			}
			reString = sb.toString();
		}finally{
			if(br!=null){
				br.close();
			}
		}
		return reString;
	}
	
	
	private static final char[] array = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
	
	/**
	 * 对数字进行62进制转换
	 * 
	 * @param number	数据
	 * @return	str
	 */
	public static String numToStringOnChar62(Integer number){
		Integer rest = number;
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder();
		while(rest!=0){
			stack.add(array[new Integer((rest-(rest/62)*62)).intValue()]);
			rest = rest/62;
		}
		for(;!stack.isEmpty();){
			result.append(stack.pop());
		}
		
		String newChar = org.apache.commons.lang3.StringUtils.leftPad(result.toString(), 6, '0');
		
		return newChar;
	}
	
}
