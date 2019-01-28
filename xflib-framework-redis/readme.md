# Redis多站点切换使用说明

> 在云化应用中，每个租户需要使用自己独立的数据库，在yml中指定站点代码custom.redis.list[].site，
> 如果每个应用需要使用多数据库，还可进一步扩充(下一版本提供)。
> 具体设置参见《典型yml配置》

## 文件清单
- /bootstrap/src/main/java/com/xflib/framework/configuration/redis/DynamicRedisConfiguration.java
- /bootstrap/src/main/java/com/xflib/framework/redis/DynamicRedisConnectionFactory.java
- /bootstrap/src/main/java/com/xflib/framework/redis/DynamicRedisHolder.java
- /bootstrap/src/main/java/com/xflib/framework/redis/DynamicRedisProperties.java
- /bootstrap/src/main/java/com/xflib/framework/redis/RedisPropertiesEx.java

## 依赖文件
- /bootstrap/src/main/java/com/xflib/framework/common/BaseException.java
- /bootstrap/src/main/java/com/xflib/framework/utils/SpringUtils.java

## 使用方法
### 注入RedisTemplate
```java
@Autowired
RedisTemplate<String, Object> redisTemplate;

redisTemplate.opsForValue().set("a", 1);
### 手动获得RedisTemplate实例
```java
RedisTemplate<String, Object> redisTemplate;

// 指定站点
redisTemplate = DynamicRedisHolder.getRedisTemplate("default");
redisTemplate.opsForValue().set("a", 1);

// 不指定站点， 即获得当前站点的RedisTemplate实例, 若果从未指定站点，将使用default
redisTemplate = DynamicRedisHolder.getRedisTemplate();
redisTemplate.opsForValue().set("a", 1);
```
### 例2
```java
private RedisTemplate<String, Object> redisTemplate;

DynamicRedisHolder.setSite("default");
redisTemplate=SpringUtils.getBean("redisTemplate",RedisTemplate.class);
redisTemplate.opsForValue().set("a", 1);

DynamicRedisHolder.setSite("30002");
redisTemplate=SpringUtils.getBean("redisTemplate",RedisTemplate.class);
redisTemplate.opsForValue().set("a", 1);
```
## 使用注意事项
- 禁用RedisAutoConfiguration可获得較好的启动速度
```java
@SpringBootApplication
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public class Startup {
    public static void main(String[] args) {
        SpringApplication.run(Startup.class, args);
    }
}
```
- 暂不支持redisRepository

# 典型yml配置
```
spring:
  redis:  # 此为站点代码=default的默认设置
    host: localhost
    password: qzkj@1234
    port: 6379
    database: 0
    pool:
      maxActive: 2000
      maxWait: -1

custom:
  redis.list:
    -
      site: default  # 优先的站点代码=default的默认设置
      config:
        host: localhost
        password: qzkj@1234
        port: 6379
        database: 1
        pool:
          maxActive: 2001
          maxWait: -1
    -
      site: 30002
      config:
        host: localhost
        password: qzkj@1234
        port: 6379
        database: 2
        pool:
          maxActive: 2002
          maxWait: -1
```