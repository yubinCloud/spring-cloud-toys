package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RestTemplateApplication {

    /**
     * 这个 Application 严格意义上并不算是一个服务消费者，因为它并没有向注册中心里面注册自己，而且在调用的时候也没有通过注册中心
     */
    public static void main(String[] args) {
        SpringApplication.run(RestTemplateApplication.class, args);
    }

    /**
     * 在 Spring Application 初始化的时候就产生一个 RestTemplate 的 bean 实例，之后可以直接从 IOC 中取出这个实例
     * @return RestTemplate 的 bean 实例，并放入 IOC 中
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}