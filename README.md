# Spring Cloud Toys

一个 Spring Cloud 的 Toys，主要作为学习使用。

本项目主要使用到的微服务组件有：

+ Eureka：用作注册中心
+ RestTemplate：对服务进行调用
+ Zuul: Gateway
+ Ribbon: 负载均衡解决方案，是一个使用 HTTP 请求进行控制的负载均衡客户端

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

## 组件介绍

### Eureka

#### 如何启动 Eureka Server？

在 Spring Boot Application 的 pom 文件中加入下面这个 dependency，就可以让这个 Application 成为一个 Eureka server:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

#### 如何在 Eureka 中注册服务？

其他的服务如果想注册进 Eureka 中，首先需要引入 Eureka Client 的依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

然后在配置文件 application.yml 中加上如下几行配置：

```yml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/  # 注册中心的访问地址
  instance:
    prefer-ip-address: true  # 是否将当前服务的 IP 注册到 Eureka Server
```

### RestTemplate

类似于 Python 的 requests，专门用于对一个 REST 服务发起调用。RestTemplate 的 bean 可以通过如下方式注入 IOC 中：

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

之后在其他 class 中直接通过 `@Autowired` 就可以得到这个 bean 了。

### Zuul

用来作为 Gateway，是客户端和⽹网站后端所有请求的中间层，对外开放一个 API，是所有请求的统一入口。

它的路由逻辑需要配置到 `application.yml` 中，比如如下一行就实现了将所有 URL 为 `/p/**` 的请求转发给 provider 服务：

```yml
zuul:
  routes:
    provider: /p/**  # 表示这个格式的请求都会转发给 provider 服务，这样就不用记 provider 的端口等信息了
```

假设 ZuulGatewayApplication 的 port 是 8030，那么请求 `localhost:8030/p/student/findAll` 的话，就会被 Zuul 转发
给 `<provider>/student/findAll` 接口。

### Ribbon

在注册中⼼心对 Ribbon 进行注册之后，Ribbon 就可以基于某种负载均衡算法，如轮询、随机、加权轮
询、加权随机等自动帮助服务消费者调⽤用接口，开发者也可以根据具体需求自定义 Ribbon 负载均衡算
法。实际开发中，Spring Cloud Ribbon 需要结合 Spring Cloud Eureka 来使用，Eureka Server 提供
所有可以调用的服务提供者列表，Ribbon 基于特定的负载均衡算法从这些服务提供者中选择要调用的
具体实例。

注意，Ribbon 本身不是一个服务，它是在一个服务中用来调用其他服务的工具。

在生成 RestTemplate 的时候，加上 `@LoadBalance` 可以实现负载均衡：

```java
@Bean
@LoadBalanced  // 声明一个基于 Ribbon 的负载均衡
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

这样，之后再使用 Ribbon 来远程调用的时候，就不需要再显式指定服务的地址和端口号了，只需要使用 service name 来代替就可以：

```java
restTemplate.getForObject("http://provider/port/index", String.class);
```

如上的代码其实就是调用了 provider 服务的 `/port/index` 接口，并通过负载均衡算法，均匀地请求多个 provider 实例。

