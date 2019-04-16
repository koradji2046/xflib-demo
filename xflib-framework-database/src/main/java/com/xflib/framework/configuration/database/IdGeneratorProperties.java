/** Copyright (c) 2019 Koradji. */
package com.xflib.framework.configuration.database;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author koradji
 * @date 2019/1/27
 */
@ConfigurationProperties(prefix = "custom.datasource")
public class IdGeneratorProperties{
	
	@PostConstruct
	public void init(){
		IdGeneratorProperties.instance=this;
	}
	
	public String getSysName() {
		return SysName;
	}
	public void setSysName(String sysName) {
		SysName = sysName;
	}
	
	public String getIdIndexUpdCycle() {
		return idIndexUpdCycle;
	}
	public void setIdIndexUpdCycle(String idIndexUpdCycle) {
		this.idIndexUpdCycle = idIndexUpdCycle;
	}

	public static IdGeneratorProperties getInstance(){
		return IdGeneratorProperties.instance;
	}
	
	private static IdGeneratorProperties instance;
	private String idIndexUpdCycle;
	private String SysName;
}
