package com.xflib.framework.configuration.amqp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.SimpleResourceHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.xflib.framework.amqp.DynamicRabbitProperties;

public abstract class AbstractAmqpAdminConfiguration {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected AmqpAdmin admin;

    @Autowired
    protected DynamicRabbitProperties dynamicRabbitProperties;

    @Autowired
    protected RabbitTemplate rabbitTemplate;

    protected List<Config> configs = new ArrayList<>();

    protected void init() {
        configs.forEach((config) -> {
            init(config);
        });
    }

    protected void init(Config config) {
        TopicExchange exchange = new TopicExchange(config.exchange);
        /*
         * 构造一个新的队列，给定名称、耐久性标志、自动删除标志和参数。参数： 
         * name - 名称队列的名称-不能为空；设置为“”以使代理生成该名称。
         * durable - 如果我们声明一个持久队列，则持久为真（该队列将在服务器重新启动后继续存在） 
         * exclusive - 如果声明独占队列，则为 true（该队列将仅由声明者的连接使用） 
         * autoDelete - 如果服务器不再使用队列时应将其删除，则自动删除为true 
         * arguments - 参数用于声明队列的参数
         */
        Queue queue = new Queue(config.queue, true, false, false, null);
        // 为每个已经建立连接的站点数据源创建exchange、queue和binding
        dynamicRabbitProperties.getSites().forEach((site) -> {
            String beanName = String.format("rabbit-%s", site);
            SimpleResourceHolder.bind(rabbitTemplate.getConnectionFactory(), beanName);
            admin.declareExchange(exchange);
            admin.declareQueue(queue);
            config.routingKeys.forEach((routeKey)->{
                admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routeKey));
            });
            SimpleResourceHolder.unbind(rabbitTemplate.getConnectionFactory());
        });
    }

    public class Config {
        public Config() {
        }

        public Config(String exchange, String queue, String... routingKey) {
            super();
            this.exchange = exchange;
            this.queue = queue;
            this.routingKeys = Arrays.asList(routingKey);
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public List<String> getRoutingKeys() {
            return routingKeys;
        }

        public void setRoutingKeys(List<String> routingKeys) {
            this.routingKeys = routingKeys;
        }

//        /**
//         * @param routingKey 可以有通配符: '*','#'. 其中'*'表示匹配一个单词, '#'则表示匹配没有或者多个单词 
//         */
//        public void addRoutingKey(String routingKey) {
//            this.routingKeys.add(routingKey);
//        }
//
//        public void removeRoutingKey(String routingKey) {
//            this.routingKeys.remove(routingKey);
//        }

        private String exchange;
        private String queue;
        private List<String> routingKeys;
    }

}
