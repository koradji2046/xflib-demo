# Spring boot 遇上 JSP

>   Spring Boot官方建议使用模板引擎，避免使用JSP! 此文献给那些依然使用JSP又要使用SpringBoot特性的程序员。

spring boot依赖spring framework，因此对 spring mvc的支持也是没说的。如果以war包部署发布是没有任何问题的，但如果以spring boot的标准jar包部署时，会遇到很多坑：

## 1 需要配置application.properties
```
#页面默认前缀目录
spring.mvc.view.prefix=/WEB-INF/jsp/
#响应页面默认后缀
spring.mvc.view.suffix=.jsp
```

# 2 不支持jstl标签, 解决办法是在pom.xml中增加依赖
```xml
<dependency>
  <groupId>org.apache.tomcat.embed</groupId>
  <artifactId>tomcat-embed-jasper</artifactId>
  <scope>provided</scope>
</dependency>
```
  
# 3 打包成jar后, jsp不能访问，解决办法是在pom.xml中增加构建属性
```xml
<resources>
	<!-- 打包时将jsp文件拷贝到WEB-INF目录下 -->
	<resource>
		<directory>src/main/webapp</directory>
		<targetPath>WEB-INF</targetPath>
		<includes>
			<include>**/**</include>
		</includes>
	</resource>
	<!-- 打包时将resources拷贝到classes目录下 -->
	<resource>
		<directory>src/main/resources</directory>
		<includes>
			<include>**/**</include>
		</includes>
		<filtering>false</filtering>
	</resource>
</resources>
```

下面是一个可编译为jar或war的标准的pom.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.qzdatasoft.web</groupId>
	<artifactId>spring-boot-sample-web-jsp</artifactId>
	<packaging>${packaging}</packaging>
	<name>Spring Boot Web JSP Sample</name>
	<description>Spring Boot Web JSP Sample</description>
	<parent>
		<groupId>com.xflib.demo</groupId>
		<artifactId>xflib-parent</artifactId>
		<version>0.0.1</version>
		<relativePath >..</relativePath> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<packaging>jar</packaging>
	</properties>
	<dependencies>
		<!-- Compile -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
		</dependency>
		<!-- Test -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

	<profiles>
		<profile>
			<id>jar</id>
			<activation>
				<activeByDefault>true</activeByDefault>
			</activation>
			<dependencies>
				<dependency>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-web</artifactId>
				</dependency>
				<dependency>
					<groupId>org.apache.tomcat.embed</groupId>
					<artifactId>tomcat-embed-jasper</artifactId>
				</dependency>
			</dependencies>
			<build>
				<finalName>${project.artifactId}-${project.version}</finalName>
				<plugins>
					<plugin>
						<groupId>org.springframework.boot</groupId>
						<artifactId>spring-boot-maven-plugin</artifactId>
					</plugin>
				</plugins>
				<resources>
					<!-- 打包时将jsp文件拷贝到WEB-INF目录下 -->
					<resource>
						<directory>src/main/webapp</directory>
						<targetPath>WEB-INF</targetPath>
						<includes>
							<include>**/**</include>
						</includes>
					</resource>
					<!-- 打包时将resources拷贝到classes目录下 -->
					<resource>
						<directory>src/main/resources</directory>
						<includes>
							<include>**/**</include>
						</includes>
						<filtering>false</filtering>
					</resource>
				</resources>
			</build>
			<properties>
				<packaging>jar</packaging>
			</properties>
		</profile>

		<profile>
			<id>war</id>
			<properties>
				<packaging>war</packaging>
			</properties>
			<dependencies>
				<dependency>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-tomcat</artifactId>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>org.apache.tomcat.embed</groupId>
					<artifactId>tomcat-embed-jasper</artifactId>
					<scope>provided</scope>
				</dependency>
				<dependency>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-web</artifactId>
					<exclusions>
						<exclusion>
							<groupId>org.springframework.boot</groupId>
							<artifactId>spring-boot-starter-tomcat</artifactId>
						</exclusion>
					</exclusions>
				</dependency>
			</dependencies>
			<build>
				<finalName>${project.artifactId}</finalName>
			</build>
		</profile>
	</profiles>

</project>
```
默认发布为jar包：mvn clean package 或者 mvn clean package -Pjar
发布为war包使用：mvn clean package -Pwar

## 4 资源文件如图片、js文件找不到

Spring Boot默认提供静态资源目录位置需置于classpath下，目录名需符合如下规则：
```xml
/static
/public
/resources
/META-INF/resources
```
举例：我们可以在src/main/resources/目录下创建static，在该位置放置一个图片文件。启动程序后，尝试访问http://localhost:8080/D.jpg。如能显示图片，配置成功。
## 5 注意事项：

spring boot 项目发布为war包，应注意：

**1 扩展SpringBootServletInitializer**

```java
public class SampleWebJspApplicationInitializer extends SpringBootServletInitializer {
 @Override
 protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
  return application.sources(SampleWebJspApplication.class); 
 }
}
```
比较简单的做法是直接扩展启动类。

```java
@SpringBootApplication
public class SampleWebJspApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {        
	return application.sources(SampleWebJspApplication.class);    
    }
    public static void main(String[] args){        
	SpringApplication.run(SampleWebJspApplication.class, args);
    }
}
```

**2 排除对spring-boot-starter-tomcat组件的引用**
```xml
<dependency> 
<groupId>org.springframework.boot</groupId> 
<artifactId>spring-boot-starter-web</artifactId>
 <exclusions>
  <exclusion>   
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-tomcat</artifactId>
  </exclusion>
 </exclusions>
</dependency>
```
**3 排除对maven插件spring-boot-maven-plugin的引用。**

[Demo下载](https://gitee.com/zhouzq2046/xflib-demo/tree/master/spring-boot-sample-web-jsp)

## 我的公众号
![微信公众号](https://mp.weixin.qq.com/mp/qrcode?scene=10000004&size=102&__biz=MzI2Nzg3NTEwNw==&mid=100000038&idx=1&sn=fbd9c879b7669b667e41e66b0f3799e6&send_time=1528526709)

