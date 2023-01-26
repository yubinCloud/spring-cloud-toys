# Spring Cloud Toys

一个 Spring Cloud 的 Toys，主要作为学习使用。

本项目主要使用到的微服务组件有：

+ Eureka：用作注册中心
+ RestTemplate：对服务进行调用
+ Zuul: Gateway
+ Ribbon: 负载均衡解决方案，是一个使用 HTTP 请求进行控制的负载均衡客户端
+ Feign: 声明式接口调用，可以以简单的方式来调用 HTTP API，可以替代 Ribbon + RestTemplate
+ Hystrix: 容错机制
+ Spring Cloud Config: 配置中心

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

### Feign

Feign 是⼀一个声明式、模版化的 Web Service 客户端，开发者可以通过简单的接⼝口和注解来调⽤用 HTTP API。
Spring Cloud Feign 整合了 Ribbon 和 Hystrix，具有可插拔、基于注解、负载均衡、服务熔断等一系列列便便捷功能。

特点：

+ Feign 是⼀一个声明式的 Web Service 客户端
+ 支持 Feign 注解、Spring MVC 注解、JAX-RS 注解
+ 基于 Ribbon 实现，使用起来更加简单
+ 集成了 Hystrix，具备服务熔断的功能

#### 使用 Feign 进行远程调用

在 pom 中加入下面这个 dependency：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

在 SpringApplication 类上加上注解 `@EnableFeignClients` 就可以使用 Feign 了。

使用方法：

1. 创建一个 interface，并通过注解 `@FeignClient` 指明所要调用的服务
2. 在 interface 中定义具体的调用接口，形式上与一个 controller 的写法相似
3. 在其他地方可以直接注入这个 feign client，并使用它来发起调用

#### Feign 开启熔断器

application.yml 中添加：

```yml
feign:
  hystrix:
    enabled: true  # 开启熔断机制
```

然后对那个定义了各个远程调用接口的 interface 写一个实现类，比如 feignexample 中的 `ProviderServiceError` 类，
它定义了各个接口在远程调用错误时的处理逻辑。完成这个实现类之后，在 interface 的 FeignClient 注解中加一个 `fallback` 的参数，并把错误处理的实现类传给该参数。
这样，当 Feign 远程调用接口失败后，就会再去调用错误处理类的相应方法。

### Hystrix

在不改变各个微服务调用关系的前提下，针对错误情况进行预先处理。

正如前面 Feign 中开启 Hystrix 的熔断器所示，它可以有效防止在调用一个服务时出现等待时间过长导致系统崩溃的现象，并可以在因一个服务出现问题时对调用者返回一个友好的提示信息。

设计原则：

+ 服务隔离机制
+ 服务降级机制
+ 熔断机制
+ 提供实时的监控和报警功能
+ 提供实时的配置修改功能

Hystrix 数据监控需要结合 Spring Boot Actuator 来使用，Actuator 提供了对服务的健康监测、数据统
计，可以通过 hystrix.stream 节点获取监控的请求数据，提供了可视化的监控界面。

这一部分要在 pom 中加的 dependency 还是挺多的，具体可以参考 hystrix 模块下的 pom.xml。
之后还需要再 Spring Boot Application 上加上几个注解，具体参考 hystrix 模块下的 HystrixApplication 类。

服务启动后，

+ 访问 `/actuator/hystrix.stream` 可以监控到请求数据
+ 访问 `/hystrix` 可以看到可视化的监控界面，输入要监控的地址节点，即可看到该节点的可视化数据监控

### Spring Cloud Config

Spring Cloud Config 通过服务端可以为多个客户端提供配置服务，
可以选择将配置文件存储在本地，
也可以将配置文件存储在远程 Git 仓库，创建 Config Server，通过它来管理所有的配置文件。

#### 存储在本地

nativeconfigserver 模块提供了对存储与本地的配置数据的 server 服务，可以以它为例。

添加依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

然后在 application.yml 中需要指定 native 的 profiles，并指定配置中心的数据路径。

在 Spring Boot Application 中要加一个 `@EnableConfigServer` 的注解。这样这个 application 就可以为其他服务返回配置文件了。

nativeconfigclient 可以读取 server 服务所提供的配置数据。它的 pom 依赖是：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

为了读取配置中心的配置文件，需要先在项目中创建一个配置文件：`bootstrap.yml`，然后在里面告诉程序从哪里读取配置文件。
具体可参考 nativeconfigclient 模块。

之后，从配置中心读取的配置文件数据就可以像仿佛这个文件是在本项目中一样了。
