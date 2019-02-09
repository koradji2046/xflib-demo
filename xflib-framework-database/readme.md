# 租用模式下Redis多站点多数据源切换模块-使用说明

> 在云化应用中，每个租户需要使用自己独立的redis数据源，而且可能每个租户的某个应用需要使用多个热地税数据源，本模块为解决这个问题而诞生。
> 在yml中指定站点代码custom.redis.sites, 这是一个字符串数组
> 在yml中通过custom.redis.list[].site为每个站点定义数据源，在程序中通过DynamicRedisHolder.setSite(String)切换站点
> 在yml中通过custom.redis.list[].sources[].source指定站点中特定的数据源，在程序中通过DynamicRedisHolder.setSource(String)切换数据源。
> spring.redis是系统默认redis数据源连接设置, 每个站点都有一个默认数据源，站点代码和数据源代码都是default，如果没有设置站点默认数据源将使用spring.redis定义的数据源作为站点默认数据源
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
### 1 注入RedisTemplate
在使用RedisTemplate<String, Object>之前指定站点和数据源，一般在filter中指定，例如：
```java
public class MyFilter implements Filter
{
    public void doFilter(...) throws ...{
      ...
      DynamicRedisHolder.setContext("30001","xk");// 设置当前线程redis上下文
      chain.doFilter();
      DynamicRedisHolder.removeContext();   // 不要忘记清理当前线程redis上下文，否则会产生内存泄露
      ...
   }
}
```
，不指定时则使用默认值或当前线程最近指定的值),注意在chain.dofilter()之后执行DynamicRedisHolder.removeContext();
```java
DynamicRedisHolder.setSite("30001");  // 指定站点
DynamicRedisHolder.setSource("xk");   // 指定数据源
```
注入RedisTemplate
```java
@Autowired
RedisTemplate<String, Object> redisTemplate;

redisTemplate.opsForValue().set("a", 1);
```
### 2 手动获得RedisTemplate实例1
```java
RedisTemplate<String, Object> redisTemplate;

// 指定站点
redisTemplate = DynamicRedisHolder.getRedisTemplate("30002","xk");
redisTemplate.opsForValue().set("a", 1);
DynamicRedisHolder.removeContext();

// 不指定站点， 即获得当前站点的RedisTemplate实例, 若果从未指定站点，将使用default
redisTemplate = DynamicRedisHolder.getRedisTemplate();
redisTemplate.opsForValue().set("a", 1);
```
### 3 手动获得RedisTemplate实例2
```java
private RedisTemplate<String, Object> redisTemplate;

DynamicRedisHolder.setSite("default");
redisTemplate=SpringUtils.getBean("redisTemplate",RedisTemplate.class);
redisTemplate.opsForValue().set("a", 1);
DynamicRedisHolder.removeContext();

DynamicRedisHolder.setSite("30002");
DynamicRedisHolder.setSource("xk")
redisTemplate=SpringUtils.getBean("redisTemplate",RedisTemplate.class);
redisTemplate.opsForValue().set("a", 1);
DynamicRedisHolder.removeContext();
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
  redis:  # 系统默认数据源(site=default 且 source=default)
    host: localhost
    password: qzkj@1234
    port: 6379
    database: 0
    pool:
      maxActive: 2000
      maxWait: -1
custom:
  redis:
    sites: default, 30001,30002,30003
    list:
      -
        site: 30001
        sources:
          -
            source: xk
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
        sources:
          -
            source: default
            config:
              host: localhost
              password: qzkj@1234
              port: 6379
              database: 1
              pool:
                maxActive: 2001
                maxWait: -1
          -
            source: xk
            config:
              host: localhost
              password: qzkj@1234
              port: 6379
              database: 1
              pool:
                maxActive: 2001
                maxWait: -1
```
通过以上设置，程序运行时会产生6个数据源：
- default-default   系统默认
- 30001-default    默认数据源(未显式定义，使用系统数据源)
- 30001-xk           另一个数据源(显式定义)
- 30002-default    默认数据源(显式定义)
- 30002-xk           另一个数据源(显式定义)
- 30003-default    默认(未显式定义，使用系统数据源)