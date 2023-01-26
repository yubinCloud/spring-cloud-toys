package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

// 这里不需要再加上 @SpringBootApplication 的注解了
@EnableZuulProxy  // 包含了 @EnableZuulServer，设置该类是网关的启动类
@EnableAutoConfiguration  // 可以帮助 Spring Boot 应用将所有符合条件的 @Configuration 配置加载当当前 SpringBoot 的 IOC 中
public class ZuulGatewayApplication {
    public static void main(String[] args)  {
        SpringApplication.run(ZuulGatewayApplication.class, args);
    }
}