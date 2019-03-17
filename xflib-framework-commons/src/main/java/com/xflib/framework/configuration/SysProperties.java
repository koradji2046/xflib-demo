package com.xflib.framework.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

//import com.qzdatasoft.framework.common.UnifyProperties;
import com.xflib.framework.utils.SpringUtils;

@Component
@ConfigurationProperties(prefix = "sys")
@PropertySource(value = { "classpath:conf/context-commom.properties" })
//@RefreshScope
public class SysProperties /*implements UnifyProperties*/{

	private static final long serialVersionUID = 1L;
	
	private static SysProperties config;

	public static SysProperties getInstance() {
		if(config == null){
			config = SpringUtils.getBean(SysProperties.class);;
		}
		return config;
	}


	private String userPermissionAuth;
	private String sysName;
	
	//id流水号更新周期
	private String idIndexUpdCycle;
	

	/**
	 * 数据权限认证需要忽略的API地址
	 */
    private String [] userPermissionAuthIgnoreUrls;
	

	/**
	 * 错误信息打印的忽略ErrorCode
	 */
    private String [] printStackTraceIgnoreErrorCode;


	/**
	 * SDK 调用方式
	 * remote：远程
	 * local：本地
	 */
	private String sdkServiceModel;

	/**
	 * 打印异常信息开关
	 * true：打印
	 * false：不打印
	 */
	private boolean printExceptionEnable;
	
	private boolean exceptionClose;
	
	
	public String getUserPermissionAuth() {
		return userPermissionAuth;
	}
	public void setUserPermissionAuth(String userPermissionAuth) {
		this.userPermissionAuth = userPermissionAuth;
	}
	public String getSysName() {
		return sysName;
	}
	public void setSysName(String sysName) {
		this.sysName = sysName;
	}
	
	public String getIdIndexUpdCycle() {
		return idIndexUpdCycle;
	}
	public void setIdIndexUpdCycle(String idIndexUpdCycle) {
		this.idIndexUpdCycle = idIndexUpdCycle;
	}
	public String getSdkServiceModel() {
		return sdkServiceModel;
	}
	public void setSdkServiceModel(String sdkServiceModel) {
		this.sdkServiceModel = sdkServiceModel;
	}
	
	public String[] getUserPermissionAuthIgnoreUrls() {
		return userPermissionAuthIgnoreUrls;
	}
	public void setUserPermissionAuthIgnoreUrls(String[] userPermissionAuthIgnoreUrls) {
		this.userPermissionAuthIgnoreUrls = userPermissionAuthIgnoreUrls;
	}
	
	public boolean getPrintExceptionEnable() {
		return printExceptionEnable;
	}
	public void setPrintExceptionEnable(boolean printExceptionEnable) {
		this.printExceptionEnable = printExceptionEnable;
	}
	
	
	public boolean isExceptionClose() {
		return exceptionClose;
	}
	public void setExceptionClose(boolean exceptionClose) {
		this.exceptionClose = exceptionClose;
	}
	/**
	 * 获取printStackTraceIgnoreErrorCode的值<br>
	 */
	public String[] getPrintStackTraceIgnoreErrorCode() {
		return printStackTraceIgnoreErrorCode;
	}
	/**
	 * 设置printStackTraceIgnoreErrorCode的值<br>
	 */
	public void setPrintStackTraceIgnoreErrorCode(String[] printStackTraceIgnoreErrorCode) {
		this.printStackTraceIgnoreErrorCode = printStackTraceIgnoreErrorCode;
	}
}
