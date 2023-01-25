package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication  // 声明该类是 Spring Boot 服务的入口
@EnableEurekaServer  // 声明该类是一个 Eureka Server 微服务，提供服务注册和服务发现功能，即注册中心
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        // 启动后访问 http://localhost:8761/ 可以看到界面
    }
}