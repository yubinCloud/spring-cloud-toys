# Spring Cloud Toys

一个 Spring Cloud 的 Toys，主要作为学习使用。

本项目主要使用到的微服务组件有：

+ Eureka：用作注册中心
+ RestTemplate：对服务进行调用
+ Zuul: Gateway

## Modules 介绍

| 模块名 | 描述 |
| :---: | :--- |
| eurekaserver | Eureka Server 服务，对外提供了注册中心的功能 |
| eurekaclient | 一个 Eureka Client，是一个服务提供方，对外提供了 Student 实体的增删查改功能 |
| resttemplate | 它使用 RestTemplate 来调用 eurekaclient 所提供的服务 |
| consumer | 服务消费者，内部调用了 `eurekaclient` 所提供的服务 |
| zuul | 使用 Zuul 的 Gateway，可以作为 proxy 并统一访问入口 |

## 项目启动顺序

1. eurekaserver
2. eurekaclient
3. consumer
4. zuul
