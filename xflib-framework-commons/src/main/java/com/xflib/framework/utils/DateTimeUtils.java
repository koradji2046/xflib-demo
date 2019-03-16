/**Copyright: Copyright (c) 2016, 湖南强智科技发展有限公司*/
package com.xflib.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期帮助类
 *
 * History:<br> 
 *    . 1.0.0.20160910, com.qzdatasoft.koradji, Create<br>
 *
 */
public class DateTimeUtils {

//	/**
//	 * 获取当前系统时间并进行格式化
//	 * @param format
//	 * @return
//	 */
//	public static String getDateTime(String format){
//		SimpleDateFormat formatter = new SimpleDateFormat(format);
//		Date curDate = new Date(System.currentTimeMillis());
//		String sDate = formatter.format(curDate);
//		return sDate;
//	}
//	
	
	/**
	 * 获取当前月的下一个月
	 * 
	 * @return 日期字符串
	 */
	public static String getYesterLogTime(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		return format.format(cal.getTime());
	}
	
	
	/**
	 * 计算指定日期是该年度第几周
	 * @param date		日期时间
	 * @return 从0开始
	 */
	public static int getWeekNumber(Date date){
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
		Calendar cl = Calendar.getInstance();   
		cl.setTime(date);   
		int week = cl.get(Calendar.WEEK_OF_YEAR);   
		//System.out.println(week);   
		cl.add(Calendar.DAY_OF_MONTH, -7);  
		int year = cl.get(Calendar.YEAR);  
		if(week<cl.get(Calendar.WEEK_OF_YEAR)){  
		    year+=1;  
		}  
		return week;
		//System.out.println(year+"年第"+week+"周"); 
	}
	
	/**
	 * 计算指定日期是哪个季度
	 * @param date		日期时间
	 * @return 0|1|2|3
	 */
	public static int getQuarter(Date date)
	{
		if(Arrays.asList(0,1, 2).contains(date.getMonth())) return 0  ;
		else if(Arrays.asList(3,4,5).contains(date.getMonth())) return 1  ;
		else if(Arrays.asList(6,7,8).contains(date.getMonth())) return 2  ;
		else return 3;
	}
	
	/**
	 * 计算指定日期是上半年还是下半年
	 * @param date			日期时间
	 * @return 0 上半年， 1 下半年
	 */
	public static int getHalfOneYear(Date date)
	{
		if(Arrays.asList(0,1, 2,3,4,5).contains(date.getMonth())) return 0  ;
		else return 1;
	}
	
	/**
	 * 返回服务器当前时间(格式yyyy-MM-dd HH:mm:ss)
	 * @return 		日期字符串
	 */
 	public static String getCurrentTime() {
		return getCurrentTime(DATETIME_FORMAT_DEFAULT);
	}
	
	/**
	 * 按照指定格式获取服务器当前日期时间
	 * @param format	格式
	 * @return			日期字符串
	 */
	public static String getCurrentTime(String format) {
		return format(new Date(),format);
	}
	
	
	/**
	 * 获取当前时间（指定日期格式）
	 * 
	 * @param format	日期格式
	 * @return			当前时间(格式化时间)
	 */
	public static Date getCurrentDate(String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		Date time=null;
		try {
		   time = sdf.parse(sdf.format(new Date()));
		} catch(Exception e) {
		   e.printStackTrace();
		}
		return time;
	}
	
	
	/**
	 * 时间格式化
	 * @param date		日期时间
	 * @param format	格式
	 * @return			日期字符串
	 */
	public static String format(Date date, String format){
		String result=""; 
		try{
			result=DateFormatUtils.format(date,format);
		}catch(Exception e){
			result=DateFormatUtils.format(new Date(), DATETIME_FORMAT_DEFAULT);
		}
		return result;
	}
	
	
	/**
	 *  时间格式化 取默认格式（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param date	日期时间
	 * @return		日期字符串
	 */
	public static String formatTime(Date date){
		String result=""; 
		try {
			result = DateFormatUtils.format(date, DATETIME_FORMAT_DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 *  时间格式化 取默认格式（yyyy-MM-dd）
	 * 
	 * @param date	日期时间
	 * @return 		日期字符串
	 */
	public static String formatDate(Date date){
		String result=""; 
		try {
			result = DateFormatUtils.format(date, DATETIME_FORMAT_DATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 将日期字符串转换为日期类型
	 * 
	 * @param dateStr	日期字符串
	 * @param format	格式
	 * @return			日期对象
	 * @throws ParseException ParseException
	 */
	public static Date parse(String dateStr, String format) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date = formatter.parse(dateStr);
		return date;	
	}
	
	
	/**
	 * 将日期字符串转换为日期类型,取默认格式(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param dateStr 	日期字符串
	 * @return			日期对象
	 */
	public static Date parseTime(String dateStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMAT_DEFAULT);
		try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	
	/**
	 * 将日期字符串转换为日期类型,取默认格式(yyyy-MM-dd)
	 * 
	 * @param dateStr 	日期字符串
	 * @return			日期对象
	 */
	public static Date parseDate(String dateStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATETIME_FORMAT_DATE);
		try {
			return formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	/**默认日期时间格式*/
	public final static String DATETIME_FORMAT_DEFAULT="yyyy-MM-dd HH:mm:ss";

	public final static String DATETIME_FORMAT_DATE="yyyy-MM-dd";
	
}
