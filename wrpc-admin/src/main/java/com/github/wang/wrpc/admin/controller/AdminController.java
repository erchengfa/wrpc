package com.github.wang.wrpc.admin.controller;

import com.github.wang.wrpc.admin.common.ZKClient;
import com.github.wang.wrpc.admin.pojo.ProviderInfo;
import com.github.wang.wrpc.common.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController {

    @Autowired
    ZKClient zkClient;

    @GetMapping("services")
    public List<String> getServices(){
        return zkClient.getChildList("/wrpc");
    }

    @GetMapping("providers")
    public List<String> getProviders(String serviceName){
        return zkClient.getChildList("/wrpc/" + serviceName + "/providers");
    }


    @GetMapping("provider-info")
    public ProviderInfo getProviderInfo(String serviceName,String path){
        String nodeData = zkClient.getNodeData("/wrpc/" + serviceName + "/providers/" + path);
        ProviderInfo providerInfo = JSONUtils.parseObject(nodeData, ProviderInfo.class);
        return providerInfo;
    }

}
