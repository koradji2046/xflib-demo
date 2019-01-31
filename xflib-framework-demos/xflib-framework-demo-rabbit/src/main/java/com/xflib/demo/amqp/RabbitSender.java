/** Copyright (c) 2019 Koradji. */
package com.xflib.demo.amqp;

import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.xflib.demo.configuration.amqp.DemoConfiguration;

/**
 * @author koradji
 * @date 2019/1/27
 */
public class RabbitSender implements CommandLineRunner {
//    private static final Log log = LogFactory.getLog(RabbitSender.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private Jackson2JsonMessageConverter jackson2JsonMessageConverter;

    public RabbitSender(){
    }

    @Override
    public void run(String... args) throws Exception {
        
        System.out.println("=> 测试类, 将直接打印输出到屏幕:");

        RabbitMessageDataBean msg=new RabbitMessageDataBean() {{
            List<String> list = Arrays.asList("a", "b", "c");
            this.setId(1);
            this.setSite("default");
            this.setData(list);
        }};
        
        String vHost=String.format("rabbit-%s", "30001");
        int count=1;
        for(int i=0;i<count;i++){
//            send(exchange,routingKey,vHost,"asdasdfasdfasd");
            send(DemoConfiguration.dynamic_test_exchange,
                    DemoConfiguration.dynamic_test_queue_routerKey,
                    vHost,msg);
        }
        System.out.println("=> 上线前请清除这个测试类: "+this.getClass().getName());
    }

    public <T> void send( String exchange, String routingKey, String vHost, T payload) {
//        Message<T> o=MessageBuilder.withPayload(payload).build();
//        RabbitMessageDataBean o1= (RabbitMessageDataBean) MessageBuilder.fromMessage(o).getPayload();
        Message data= jackson2JsonMessageConverter.toMessage(payload, new MessageProperties());
        data.getMessageProperties().setType(RabbitMessageDataBean.class.getName());
        data.getMessageProperties().setHeader("__TypeId__", RabbitMessageDataBean.class.getName());
        SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), vHost);
        rabbitTemplate.convertAndSend(exchange,routingKey,data);
        SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
    }

}
