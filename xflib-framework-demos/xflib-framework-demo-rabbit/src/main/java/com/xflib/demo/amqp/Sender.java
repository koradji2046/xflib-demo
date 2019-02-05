/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.amqp;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.xflib.framework.amqp.DynamicRabbitConnectionFactory;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class Sender implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private DynamicRabbitConnectionFactory conn;
    
    @Override
    public void run(String... args) throws Exception {
        
        log.info("=> 测试类, 将直接打印输出到屏幕:");

        DataBean msg=new DataBean();
        msg.setId(1);
        msg.setSite("default");
        msg.setData(Arrays.asList("a", "b", "c"));

        String vHost=String.format("rabbit-%s", "default");
        int count=1000;
        for(int i=0;i<count;i++){
            try{
                msg.setId(i);
                conn.send(Constants.dynamic_test_exchange,
                        Constants.dynamic_test_queue_routingKey,
                    vHost,msg);
                Thread.sleep(1000);
            }catch(InterruptedException e){
                Thread.interrupted();
            }catch(Exception e){
                log.error(e.getMessage());
            }
        }
        log.info("=> 上线前请清除这个测试类: "+this.getClass().getName());
    }

}
