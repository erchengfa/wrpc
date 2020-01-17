package com.github.wang.wrpc.context.test.remoting.netty;

import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.remoting.DefaultFuture;
import com.github.wang.wrpc.context.remoting.netty.NettyClient;


/**
 * @author : wang
 * @date : 2019/12/29
 */
public class TestClient {

    public static void main(String[] args) throws Throwable {

        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setProtocol("wang");
        providerInfo.setHost("127.0.0.1");
        providerInfo.setPort(28000);

        NettyClient client = new NettyClient(providerInfo);
        client.connect();

        for (int i=0; i < 100000;i++){
            Request request = new Request();
            Invocation requestBody = new Invocation();
            requestBody.setServiceName("hello");
            requestBody.setServiceVersion("0.0");
            requestBody.setParameters(new Object[]{"zs"});
            requestBody.setParameterTypes(new Class[]{String.class});
            requestBody.setMethodName("sayHello");
            request.setBody(requestBody);
            request.setHeartbeat(false);
            DefaultFuture defaultFuture =  DefaultFuture.newFuture(request,5000L);
            client.send(request);
            System.out.println(defaultFuture.get());
        }

        System.in.read();
    }

}
