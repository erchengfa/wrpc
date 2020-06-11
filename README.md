# wrpc

一个轻量级rpc框架：

1、基于Netty通信：断线重连、长连接、心跳机制。

2、spi机制可扩展。

3、采用高性能框架Disrupor对消息进行处理。

4、序列化方式：kryo

5、集群机制：failover（失败重试）、failfast(快速失败)

6、负载均衡：random（加权随机）、roundrobin（轮训）

7、注册中心支持：zookeeper、nacos



快速开始：

暴露服务

```java
        //注册中心
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");//协议
        registryConfig.setAddress("118.89.196.99:2181");//注册中心地址
        //配置服务
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(20801);        //设置端口
        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo1");//设置应用名
        providerConfig.setInterfaceClass(IDemoService.class);//设置接口类
        providerConfig.setServiceBean(new DemoServiceImpl());//设置服务实现类
        providerConfig.setServer(serverConfig);//设置服务
        providerConfig.setRegistry(registryConfig);//设置注册中心
        providerConfig.export();//暴露服务
```

消费服务

```java
        //注册中心
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");//协议
        registryConfig.setAddress("118.89.196.99:2181");//注册中心地址
        ConsumerConfig<IDemoService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setInterfaceClass(IDemoService.class);//设置接口类
        consumerConfig.setRegistry(registryConfig);//设置注册中心
        consumerConfig.setAppName("consumer");//设置应用程序
        IDemoService iDemoService = consumerConfig.refer();//获取远程代理类
        while (true){
            try {
                System.out.println(iDemoService.sayHello("wang"));
                Thread.sleep(1000);
            }catch (Exception e){
            }
        }
```



springboot 示例在wcp-example下。