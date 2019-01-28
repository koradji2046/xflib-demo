# Redis多站点切换使用说明

## 文件清单
- /bootstrap/src/main/java/com/xflib/framework/configuration/redis/DynamicRedisConfiguration.java
- /bootstrap/src/main/java/com/xflib/framework/redis/DynamicRedisConnectionFactory.java
- /bootstrap/src/main/java/com/xflib/framework/redis/DynamicRedisHolder.java
- /bootstrap/src/main/java/com/xflib/framework/redis/DynamicRedisProperties.java
- /bootstrap/src/main/java/com/xflib/framework/redis/RedisConnectionFactoryProxy.java
- /bootstrap/src/main/java/com/xflib/framework/redis/RedisPropertiesEx.java

## 依赖文件
- /bootstrap/src/main/java/com/xflib/framework/common/BaseException.java
- /bootstrap/src/main/java/com/xflib/framework/utils/SpringUtils.java

## 使用方法
### 例1
```java
RedisTemplate<String, Object> redisTemplate;

redisTemplate = DynamicRedisHolder.getRedisTemplate("default");
redisTemplate.opsForValue().set("a", 1);

redisTemplate = DynamicRedisHolder.getRedisTemplate("30002");
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
- 禁用RedisAutoConfiguration
```java
@SpringBootApplication
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public class Startup {
    public static void main(String[] args) {
        SpringApplication.run(Startup.class, args);
    }
}
```
- RedisTemplate<String, Object>不支持注入
- 暂不支持redisRepository

# 典型yml配置
```
spring:
  redis:
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
      site: 30001
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