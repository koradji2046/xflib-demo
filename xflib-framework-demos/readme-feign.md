feign开发说明
================

1 feign接口类以及configuration配置类不要在component搜索路径上
2 configuration配置类上不要使用@Configuration注解
3 在启动类上使用使用@EnableFeignClients(basePackages={"com.xflib.*.feign.sdk"})来指定feign接口类的专用搜索路径
4 不遵循以上规则，可能会出现各种各样的异常！
5 不适用eureka时，@FeignClient请使用name或value和url参数，对于相同host而feign接口类不同的接口，建议使用不同的name或value

按照上述规则：
1 没有出现RequestInterceptor相互覆盖的行为。
2 没有出现路径不识别的行为。

## 范例工程：
spring-boot-feign-customer
spring-boot-feign-sdk
spring-boot-feign-service