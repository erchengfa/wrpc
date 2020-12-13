# 概述

一个轻量级rpc框架：

1、基于Netty通信：断线重连、长连接、心跳机制。

2、spi机制可扩展。

3、采用高性能框架Disrupor对消息进行处理。

4、序列化方式：kryo

5、集群机制：failover（失败重试）、failfast(快速失败)

6、负载均衡：random（加权随机）、roundrobin（轮训）

7、注册中心支持：zookeeper【版本： 3.5.7】、nacos【版本： 1.3.1】



# 添加依赖

安装到mvn仓库

```java
git clone https://github.com/wang-hello/wrpc.git
mvn clean install

```



添加依赖

```
<dependency>
    <groupId>com.github.wang.wrpc</groupId>
    <artifactId>wrpc-boot-starter</artifactId>
    <version>${wrpc.version}</version>
</dependency>
```



# springboot 使用

## 服务提供者

application.properties配置：

```yaml
wrpc.enabled=true #开启wrpc
wrpc.appName=demo-provider # 应用名称
wrpc.registry.address=127.0.0.1:2181 # 注册中心地址
wrpc.registry.protocol=zookeeper # 注册中心协议 
wrpc.server.protocol=wang #wrpc协议
wrpc.server.port=20800 # wrpc监听端口
```

暴露服务:@RpcService(value = IHelloService.class)注解标识，value为服务接口类。

```java
@RpcService(value = IHelloService.class)
public class HelloService1 implements IHelloService {
    @Override
    public String sayHello(String name) {
        return "welcome " + name;
    }
}
```

添加版本:下面指定版本号为v2

```java
@RpcService(value = IHelloService.class,version = "v2")
public class HelloService2 implements IHelloService {
    @Override
    public String sayHello(String name) {
        return "welcome v2 " + name;
    }
}
```



## 服务消费者

application.properties配置

```java
wrpc.enabled=true
wrpc.appName=demo-consumer
wrpc.registry.address=127.0.0.1:2181
wrpc.registry.protocol=zookeeper
```

采用@RpcReference注入rpc服务

```java
@RestController
public class HelloController {
    @RpcReference
    private IHelloService iHelloService;
    @RpcReference(version = "v2")
    private IHelloService iHelloServiceV2;

    @GetMapping("/say-hello")
    public String syaHello() {
        return iHelloService.sayHello("wrpc");
    }

    @GetMapping("/say-hello-v2")
    public String syaHelloV2() {
        return iHelloServiceV2.sayHello("wrpc");
    }

}
```



详细示例代码wrpc-example，示例采用nacos作为注册中心。