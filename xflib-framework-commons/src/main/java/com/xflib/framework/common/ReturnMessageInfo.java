/**Copyright: Copyright (c) 2016, Koradji */
package com.xflib.framework.common;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

//import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xflib.framework.utils.DateTimeUtils;

/**
 * 
 * 通用数据传输结构<br>
 * errorCode 0表示成功，小于0表示出错，大于0由业务定义
 * 
 * History:<br>
 *  . 1.0.0.20160910, Koradji, Create<br>
 *  . 20170531, wangshi, 增加方法：setErrorCode(BaseException e)，处理带参数异常信息    <br>
 *  . 20171227, koradji, fixed：对messageUtils.getmessage()的调用    <br>
 * 
 */
@SuppressWarnings("serial")
@XmlRootElement
public class ReturnMessageInfo implements java.io.Serializable{
//	private static Logger log = LoggerFactory.getLogger(ReturnMessageInfo.class);
	private String errorCode = Constants.ERROE_CODE_DEFAULT; // success成功,
																// UnknownException未定义或未初始化的错误代码,
																// 其他由业务定义
	private String errorMessage = Constants.ERROE_CODE_DEFAULT_MESSAGE; // errorCode的详细信息
	private Object [] errorMessageParam = null; // 对应errorMessage中的点位符%s
	private Object data = null; // 数据
	

	private String runTime = ""; // 运行时间

	private long runStartTime = 0; // 运行开始时间
	private long runEndTime = 0; // 运行结束时间

	@JsonIgnore
	@XmlTransient
	private boolean customMessageCode=false;
	
	/**
	 * 构造函数
	 */
	public ReturnMessageInfo() {
		runStartTime = new Date().getTime();
	}

	/**
	 * 构造函数
	 * 
	 * @param code	 编号
	 */
	public ReturnMessageInfo(String code) {
		this(code, null);
		runStartTime = new Date().getTime();
	}
	
	
	/**
	 *  构造函数
	 * 
	 * @param isCustomErrorCode	是否自定义编号
	 */
	public ReturnMessageInfo(boolean isCustomErrorCode) {
		runStartTime = new Date().getTime();
		this.customMessageCode=isCustomErrorCode;
	}

//	/**
//	 * 构造函数
//	 * 
//	 * @param code
//	 * @param message
//	 */
//	public ReturnMessageInfo(String code, String message) {
//		this(code, message, null);
//	}

	/**
	 * 构造函数
	 * 
	 * @param code		代码
	 * @param data		数据内容
	 */
	public ReturnMessageInfo(String code, Object data) {
		runStartTime = new Date().getTime();
		this.setErrorCode(code);
		this.data = data;
//		this( code, null, data);
	}

//	/**
//	 * 构造函数
//	 * 
//	 * @param code
//	 * @param message
//	 * @param data
//	 */
//	public ReturnMessageInfo(String code, String message, Object data) {
//		this.setErrorCode(code);
//		this.data = data;
//	}

	/**
	 * 获取错误代码
	 * 
	 * @return	str
	 */
	public String getErrorCode() {
		return this.errorCode;
	}
	
	/**
	 * 设置错误代码
	 * 
	 * @param errorCode		错代码
	 * @param args		参数
	 */
	public void setErrorCode(String errorCode, Object ...args) {
//		try {
			this.errorCode = errorCode;
			this.errorMessage = errorCode;
			this.errorMessageParam = args;
//			this.errorMessage = (errorCode.equals(Constants.ERROE_CODE_DEFAULT) ? Constants.ERROE_CODE_DEFAULT_MESSAGE:(
//					(errorCode.equals(Constants.ERROE_CODE_SUCCESS) ? Constants.ERROE_CODE_SUCCESS_MESSAGE:(
//						this.customMessageCode?errorCode:MessageUtils.getMessage(errorCode,RequestHolder.createAnonymouse())//20171227, koradji
//					)
//				))
//			);
//			
//			if(!errorMessage.equals(errorCode) && args!=null){
//				this.errorMessage=String.format(this.errorMessage, args);
//			}
			setRunTime();
//		} catch (Exception e) {
//			log.error("", e);
//			ExceptionUtils.printStackTrace(e);
//		}
	}
	
	/**
	 * 设置错误代码
	 * 处理带参数异常信息
	 * @param e		异常信息
	 */
	public void setErrorCode(BaseException e) {
		setErrorCode(e.getMessage(), e.getMessageParam());
	}

	/**
	 * 获取错误信息
	 * 
	 * @return	str
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	/**
	 * 设置错误信息&lt;br&gt;
	 * 只有在使用ReturnMessageInfo(true)初始化偶可以使用这个方法！
	 * 
	 * @param errorMessage 自定义错误消息
	 */
	public void setErrorMessage(String errorMessage) {
		if(this.customMessageCode)
			this.errorMessage = errorMessage;
//		else
//			ExceptionUtils.printStackTrace(new Exception("setErrorMessage when returnMessageInfo.customMessage is false!"));
	}

	/**
	 * 获取数据对象
	 * 
	 * @return Object	数据内容
	 */
	@XmlTransient
	public Object getData() {
		return this.data;
	}

	/**
	 * 设置数据对象
	 * 
	 * @param data	数据内容
	 */
	public void setData(Object data) {
		this.data = data;
	}

//	/**
//	 * 获取customMessageCode的值<br>
//	 */
//	public boolean isCustomMessageCode() {
//		return customMessageCode;
//	}

//	/**
//	 * 设置customMessageCode的值<br>
//	 */
//	public void setCustomMessageCode(boolean isCustomMessageCode) {
//		this.customMessageCode = isCustomMessageCode;
//	}
//
//	/**
//	 * 设置customMessageCode的值<br>
//	 * 注意：仅在customMessageCode=true情况下有效
//	 */
//	public void setCustomErrorCode(String customMessageCode) {
//		if(this.customMessageCode)
//			this.errorMessage = (customMessageCode == Constants.ERROE_CODE_DEFAULT) 
//				? Constants.ERROE_CODE_DEFAULT_MESSAGE
//				: ((errorCode == Constants.ERROE_CODE_SUCCESS) 
//						? Constants.ERROE_CODE_SUCCESS_MESSAGE
//						: "");
//	}
	
//	/**
//	 * 设置错误代码
//	 * 
//	 * @param errorCode
//	 */
//	public void setCustomErrorInfo(String errorCode, String errorMessage) {
//		if(StringUtils.isBlank(errorMessage)){
//			setErrorCode(errorCode);
//		}else{
//			this.errorCode = errorCode;
//			this.errorMessage=errorMessage;
//		}
//	}
	
	private void setRunTime(){
		runEndTime = new Date().getTime();
		runTime += "开始时间：" + DateTimeUtils.formatTime(new Date(runStartTime));
		runTime += ";结束时间：" + DateTimeUtils.formatTime(new Date(runEndTime));
		runTime += ";运行时间：" + (runEndTime - runStartTime) + "毫秒";
	}

	public String getRunTime() {
		return runTime;
	}

	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}
	
	
	/**
	 * 获取errorMessageParam的值<br>
	 */
	public Object[] getErrorMessageParam() {
		return errorMessageParam;
	}

	/**
	 * 设置errorMessageParam的值<br>
	 */
	public void setErrorMessageParam(Object[] errorMessageParam) {
		this.errorMessageParam = errorMessageParam;
	}

//	public String toJson(){
//		String out="";
//		try{
//			JSONObject jsonResult = JSONObject.fromObject(this);
//			out=jsonResult.toString();
//		}catch(Exception e1){
////			Log.warn("message.toJson.failed");
//			Log.warn("将消息对象转换为json字符串失败");
//		}
//		
//		return out;
//	}
//	
//	public static ReturnMessageInfo fromJson(String json){
//		ReturnMessageInfo result=null;
//		try{
//			result= new ObjectMapper().readValue(json, ReturnMessageInfo.class);
////			JSONObject jsonobject = JSONObject.fromObject(result.getData());
////			VirtualTestUnit.ip=	jsonobject.getString("ip");
////			Aaa aaa=(Aaa)JSONObject.toBean(jsonobject, Aaa.class);
//		}catch(Exception e){
//			Log.warn("json字符串转换为将消息对象失败");
//		}
//		
//		return result;
//	}
}
