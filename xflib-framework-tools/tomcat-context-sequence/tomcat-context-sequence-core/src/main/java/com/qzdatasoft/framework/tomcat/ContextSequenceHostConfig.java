package com.qzdatasoft.framework.tomcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.startup.HostConfig;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Created by koradji on 2018-6-7.
 */
public class ContextSequenceHostConfig extends HostConfig {
    /** context 启动顺序属性配置文件 */
    private static final String DEPLOY_ORDER_PROP = "/context-sequence.properties";
    private static final Log log = LogFactory.getLog(ContextSequenceHostConfig.class);

    /**
     * 加载顺序属性文件
     */
    private List<String> loadOrderProperties() {
    	List<String> orderFiles = new ArrayList<String>();
        InputStream resourceAsStream = this.getClass().getResourceAsStream(DEPLOY_ORDER_PROP);
        if (resourceAsStream == null){
        	log.warn("file not found: "+DEPLOY_ORDER_PROP);
        }else{
        	BufferedReader in2=new BufferedReader(new InputStreamReader(resourceAsStream));
            try {
            	String y="";
            	while((y=in2.readLine())!=null){//一行一行读
            		orderFiles.add(y);
            	}
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                try {
                    in2.close();
                    resourceAsStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return orderFiles;
    }
    
    // 不支持自定义目录，必须部署在webapps目录下面
    @Override
    protected void deployApps() {

    	List<String> orderFiles=loadOrderProperties();
    	for(String appName :orderFiles){
    		deployApps(appName);
    	}	
    	
    	super.deployApps();

    }

}