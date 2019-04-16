package com.xflib.framework.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xflib.framework.common.BaseException;

/**
 * 获取请求工具<br>
 * <br>
 * History:<br>
 * . v1.0.0.20180122, WangShi, Create<br>
 *   增加getRequest方法的异常处理
 */
public abstract class HttpRequestContextUtils {
  
	/**
	 * 获取当前线程Request对象
     * @return {@link HttpServletRequest}
	 * @throws BaseException
	 */
    public static HttpServletRequest getRequest() throws BaseException {
		try{
	        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		}catch(Exception e){
			throw new BaseException(HttpRequestContextUtils.class,"HTTP.request.isNull",e);
		}
    }
   
    /**
     * 获取当前线程Response对象
     * @return {@link HttpServletResponse}
	 * @throws BaseException
     * @return
     */
    public static HttpServletResponse geResponse() {
		try{
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		}catch(Exception e){
			throw new BaseException(HttpRequestContextUtils.class,"HTTP.response.isNull",e);
		}
    }
    
    /**
     * 获取请求的参数：header，param，attr
     * 注意: 本方法不能获取在request body中设置的参数
     * @param request
     * @param key
     * @return
     */
    public static String resolve(HttpServletRequest request, String key) {
		String result="";
		result= request.getHeader(key);
		if(StringUtils.isBlank(result)){
			result= request.getParameter(key);
			if(StringUtils.isBlank(result)){
				result= StringUtils.toString(request.getAttribute(key));
			}
		}
		return result;
	}
}
