package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients  // 使用 Feign
public class FeignExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignExampleApplication.class, args);
    }
}