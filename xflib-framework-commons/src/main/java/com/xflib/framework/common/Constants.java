/**Copyright: Copyright (c) 2016, Koradji*/
package com.xflib.framework.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常量
 *
 * History:<br> 
 *    . v1.0.0.20180105, Koradji, Added: 增加了几个常量<br>
 *    . v1.0.0.20170805, Koradji, Create<br>
  */
public class Constants {

	private static Logger log = LoggerFactory.getLogger(Constants.class);

	/** 操作系统换行符 */
	public static final String LineSeparator = (String) java.security.AccessController.doPrivileged(
             new sun.security.action.GetPropertyAction("line.separator"));

	 public static final String ERROE_CODE_SUCCESS					="success";
	 public static final String ERROE_CODE_SUCCESS_MESSAGE	="Success!";
	 public static final String ERROE_CODE_DEFAULT					="unknownException";
	 public static final String ERROE_CODE_DEFAULT_MESSAGE	="Unknown Exception!";
	 
	 public static final int CURRENT_PAGE_DEFAULT					= 1; 
	 public static final int PAGESIZE_DEFAULT							= 30; 
	 public static final String CURRENT_PAGE_DEFAULT_STR		= "1"; 
	 public static final String PAGESIZE_DEFAULT_STR					= "30"; 
	 
	 /** Session 当前语种 关键字*/
	public static final String SESSION_KEY_LANGUAGE_ID = "XFLIB_FRAMEWORK_SESSION_KEY_LANGUAGE";
	 /**  Session Site ID*/
	 public static final String SESSION_KEY_SITE_ID 					= "XFLIB_FRAMEWORK_SESSION_KEY_SITE";
	 /** Session  当前用户从哪一个APP登录*/
	 public static final String SESSION_KEY_APP_ID					= "XFLIB_FRAMEWORK_SESSION_KEY_APP_ID";
	 /** Session  当前登录用户代码*/
	 public static final String SESSION_KEY_LOGIN_IDENTITY	= "XFLIB_FRAMEWORK_SESSION_KEY_LOGIN_IDENTITY";

	 /** 
	  * Session  当前登录用户可使用的角色权限列表,数据类型：Map(String, List(Map(String, String)))
	  * map.put(SysPermissionBean.USER_PERMISSION,,List(Map))
	  * map.put(SysPermissionBean.AGENT_PERMISSION,,List(Map))
	  * map.put("roleCode",List&lt;Map&gt; )
	  * list.map.put("RESOURCE",RESOURCE)
	  * list.map.put("INSTANCE",INSTANCE)
	  * */
	 public static final String SESSION_KEY_USER_ROLE_PERMISSION	= "XFLIB_FRAMEWORK_SESSION_KEY_USER_ROLE_PERMISSION";
	 public static final String SESSION_KEY_USER_CURRENT_PERMISSION	= "XFLIB_FRAMEWORK_SESSION_KEY_USER_CURRENT_PERMISSION";

	 /** 
	  * Session  当前登录用户可使用的菜单资源,数据类型：Map&lt;String, List&lt;Map&lt;String, Object&gt; &gt; &gt; 
	  * map.put("roleCode",menuTree)
	  * 这里只存储了Code，不存储具体的权限值
	  * */
	 public static final String SESSION_KEY_USER_ROLE_MENU	= "XFLIB_FRAMEWORK_SESSION_KEY_USER_ROLE_MENU";
	 public static final String SESSION_KEY_USER_CURRENT_MENU	= "XFLIB_FRAMEWORK_SESSION_KEY_USER_CURRENT_MENU";

	 /** 
	  * 用户当前角色
	  * SysPermissionBean.USER_PERMISSION/roleCode
	  * */
	 public static final String SESSION_KEY_USER_CURRENT_ROLE	= "XFLIB_FRAMEWORK_SESSION_KEY_USER_CURRENT_ROLE";

	 /** 
	  * 用户当前代理的用户
	  * userAccount
	  * 没有代理就是空，有代理就是用户帐号信息
	  * */
	 public static final String SESSION_KEY_USER_CURRENT_AGENT	= "XFLIB_FRAMEWORK_SESSION_KEY_USER_CURRENT_AGENT";
	 
	 /** Session  当前登录用户坐在HR组织的代码*/
	 public static final String SESSION_KEY_LOGIN_ORG_HR		= "XFLIB_FRAMEWORK_SESSION_KEY_LOGIN_ORG_HR";
	 /** Session SSO 令牌 */
	 public static final String SESSION_KEY_TOKEN					= "XFLIB_FRAMEWORK_SESSION_KEY_TOKEN";
	 /** Session RSA 密钥对 关键字*/
	 public static final String SESSION_KEY_RSA						= "XFLIB_FRAMEWORK_SESSION_KEY_RSA";
//	 public static final String SESSION_KEY_AUTH_CODE			= "XFLIB_FRAMEWORK_SESSION_KEY_AUTH_CODE";
//	 public static final String SESSION_KEY_RSA_PRIVATE		= "XFLIB_FRAMEWORK_SESSION_KEY_RSA_PRI";
//	 public static final String SESSION_KEY_RSA_PUBLIC			= "XFLIB_FRAMEWORK_SESSION_KEY_RSA_PUB";


	 /** http请求专用身份验证码关键字 */
	 public static final String HTTP_REQUEST_HEADER_AUTHCODE="authcode";
	 /** http请求专用app关键字 */
	 public static final String HTTP_REQUEST_HEADER_APP_ID="app";
	 /** http请求专用siteId关键字 */
	 public static final String HTTP_REQUEST_HEADER_SITE_ID="site";
	 /** Session  语种*/
	 public static final String HTTP_REQUEST_HEADER_LANGUAGE_ID 	= "locale";
	 /** userCode 用户登录名*/
	 public static final String HTTP_REQUEST_HEADER_LOGIN_IDENTITY = "identity";
	 /** token */
	 public static final String HTTP_REQUEST_HEADER_TOKEN = "accessToken";
	 /** DATA_ROWID_PREFIX, 用于区分数据来源于哪个gateway */
	 public static final String  HTTP_REQUEST_HEADER_DATA_ROWID_PREFIX ="DATA_ROWID_PREFIX";
	/**  JWT */
	public static final String HTTP_REQUEST_HEADER_JWTTOKEN="TOKEN";
	/** 是否白名单请求 */
	public static final String HTTP_REQUEST_HEADER_ISIGNORINGURL="isIgnoringUrl";
	/** 是否校验token */
	public static final String HTTP_REQUEST_HEADER_QZ_URL_BY_GW="QZ_URL_BY_GW";
	/** 是否前端传入SITE */
	public static final String HTTP_REQUEST_HEADER_QZ_SITE_BY_GW="QZ_SITE_BY_GW";

	 
	 /** 验证码的生命周期关键字 */
	 public static final String SESSION_KAPTCHA_LYFECYCLE_KEY				= "SESSION_KAPTCHA_LYFECYCLE";
	 /** 验证码的生命周期(秒) */
	 public static final int SESSION_KAPTCHA_LYFECYCLE_VALUE				= 180;
	 /** 验证码输入错误时可重试次数关键字 */
	 public static final String SESSION_KAPTCHA_RETYR_TIMES_KEY			= "SESSION_KAPTCHA_RETYR_TIMES";
	 /** 验证码输入错误时可重试次数 */
	 public static final int SESSION_KAPTCHA_RETYR_TIMES_MAX_VALUE			= 3;

	 /** 默认的site值 */
	 public static final String DEFAULT_SITE_ID="default";// TODO:应该为default
	 /** 默认的app值 */
	 public static final String DEFAULT_APP_ID="PCWEB";
	 /** 默认的identity值 */
	 public static final String DEFAULT_IDENTITY_ID="internal";
	 /** 默认的authcode值 */
	 public static final String DEFAULT_AUTHCODE="internal";

	/**
	 * 默认的开发者帐号
	 */
	public static final String SYS_USER_DEVELOPER = "developer";

	/* 授权许可-用户代码*/ 
	public static final String LIC_CUSTOMER_CODE	="LIC_CUSTOMER_CODE";

	 // 测试用的
//	 public enum testMapType{BeanLoadOrder};
//	 public static Function<testMapType, Integer> AssertOrder=new Function<testMapType, Integer>(){
//			public Map<testMapType,Integer>map=new HashMap<testMapType,Integer>(){
//				private static final long serialVersionUID = 2786270984880217066L;
//				{put(testMapType.BeanLoadOrder,0);}
//			};
//			@Override
//			public synchronized Integer apply(testMapType t) {
//				Integer re=null;
//				switch(t){
//				case BeanLoadOrder:
//					re=map.get(testMapType.BeanLoadOrder);
//					log.info(String.format("%s: %d", StackTraceUtils.getParentMethodFullName(4),re));
//					map.put(testMapType.BeanLoadOrder, re+1);
//				break;
//				}
//				return re;
//			}
//	 };
	
	public static final String YML_SysName="";
	public static final String YML_IdGenerateorCacheCapacity="sys.idIndexUpdCycle";
	public static final String YML_IdPrefix="";
}
