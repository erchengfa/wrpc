package com.github.wang.registry.zk;


import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.CommonUtils;
import com.github.wang.wrpc.common.utils.JSONUtils;
import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.common.utils.UrlUtils;
import com.github.wang.wrpc.context.common.RegistryUtils;
import com.github.wang.wrpc.context.common.RpcConstants;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.RpcDefaultConfig;
import com.github.wang.wrpc.context.observer.ProviderObserver;
import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.wang.wrpc.common.utils.StringUtils.CONTEXT_SEP;



@Slf4j
public class ZookeeperRegistry extends Registry {

    /**
     * 接口配置{ConsumerConfig：PathChildrenCache} <br>
     * 例如：{ConsumerConfig ： PathChildrenCache }
     */
    private static final ConcurrentMap<ConsumerConfig, PathChildrenCache> INTERFACE_PROVIDER_CACHE = new ConcurrentHashMap<ConsumerConfig, PathChildrenCache>();


    private static final ConcurrentMap<ConsumerConfig, NodeCache> INTERFACE_CONSUMER_CACHE = new ConcurrentHashMap<ConsumerConfig, NodeCache >();

    /**
     * Zookeeper zkClient
     */
    private CuratorFramework zkClient;

    private ConcurrentHashMap<String,ProviderObserver> observers = new ConcurrentHashMap<>();

    /**
     * 注册中心配置
     *
     * @param registryConfig 注册中心配置
     */
    protected ZookeeperRegistry(RegistryConfig registryConfig) {
        super(registryConfig);
        String address = registryConfig.getAddress();

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFrameworkFactory.Builder zkClientuilder = CuratorFrameworkFactory.builder()
                .connectString(address)
                .sessionTimeoutMs(registryConfig.getConnectTimeout() * 3)
                .connectionTimeoutMs(registryConfig.getConnectTimeout())
                .canBeReadOnly(false)
                .retryPolicy(retryPolicy)
                .defaultData(null);
//        //是否需要添加zk的认证信息
//        List<AuthInfo> authInfos = buildAuthInfo();
//        if (CommonUtils.isNotEmpty(authInfos)) {
//            zkClientuilder = zkClientuilder.aclProvider(getDefaultAclProvider())
//                    .authorization(authInfos);
//        }

        zkClient = zkClientuilder.build();

        zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {

                if (log.isInfoEnabled()) {
                    log.info("reconnect to zookeeper,recover provider and consumer data");
                }
            }
        });

    }

    /**
     * 获取默认的AclProvider
     *
     * @return
     */
    private ACLProvider getDefaultAclProvider() {
        return new ACLProvider() {
            @Override
            public List<ACL> getDefaultAcl() {
                return ZooDefs.Ids.CREATOR_ALL_ACL;
            }

            @Override
            public List<ACL> getAclForPath(String path) {
                return ZooDefs.Ids.CREATOR_ALL_ACL;
            }
        };
    }


    /**
     * 创建认证信息
     *
     * @return
     */
    private List<AuthInfo> buildAuthInfo() {
        List<AuthInfo> info = new ArrayList<AuthInfo>();

        String scheme = registryConfig.getParameter("scheme");

        //如果存在多个认证信息，则在参数形式为为addAuth=user1:paasswd1,user2:passwd2
        String addAuth = registryConfig.getParameter("addAuth");

        if (StringUtils.isNotEmpty(addAuth)) {
            String[] addAuths = addAuth.split(",");
            for (String singleAuthInfo : addAuths) {
                info.add(new AuthInfo(scheme, singleAuthInfo.getBytes()));
            }
        }

        return info;
    }

    @Override
    public synchronized boolean start() {
        if (zkClient == null) {
            log.warn("Start zookeeper registry must be do init first!");
            return false;
        }
        if (zkClient.getState() == CuratorFrameworkState.STARTED) {
            return true;
        }
        try {
            zkClient.start();
        } catch (Exception e) {
            throw new RPCRuntimeException("Failed to start zookeeper zkClient", e);
        }
        return zkClient.getState() == CuratorFrameworkState.STARTED;
    }

    private CuratorFramework getAndCheckZkClient() {
        if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
            throw new RPCRuntimeException("Zookeeper client is not available");
        }
        return zkClient;
    }

    @Override
    public void register(ProviderConfig config) {
        String providerPath = RegistryUtils.buildProviderPath(config.getServiceName());
        List<ProviderInfo> providerInfos = RegistryUtils.convertProviderInfos(config);
        try {
            for (ProviderInfo providerInfo : providerInfos) {
                String providerUrl = providerPath + CONTEXT_SEP + UrlUtils.getURLEncoderString(providerInfo.getUrl());
                log.debug("register providerUrl:{}", providerUrl);
                getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.EPHEMERAL) // 临时节点
                        .forPath(providerUrl, providerInfo.convertData()); //节点 + 动态变更的数据
            }
        } catch (Exception e) {
            throw new RPCRuntimeException("Failed register to zookeeper", e);
        }

    }

    @Override
    public ProviderGroup subscribe(ConsumerConfig config) {
        //订阅如果有必要
        subscribeConsumerUrls(config);
        // 订阅Providers节点
        try {
            final String providerPath = RegistryUtils.buildProviderPath(config.getServiceName());
            PathChildrenCache pathChildrenCache = INTERFACE_PROVIDER_CACHE.get(config);
            if (pathChildrenCache == null) {
                pathChildrenCache = new PathChildrenCache(zkClient, providerPath, true);
                final PathChildrenCache finalPathChildrenCache = pathChildrenCache;
                pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        if (log.isDebugEnabled()) {
                            log.debug("Receive zookeeper event: {},{}", config.getAppName(),finalPathChildrenCache.getCurrentData());
                        }
                        switch (event.getType()) {
                            case CHILD_ADDED: //加了一个provider
                                notifyObserver(toProviderGoup(config, providerPath, finalPathChildrenCache.getCurrentData()));
                                break;
                            case CHILD_REMOVED: //删了一个provider
                                notifyObserver(toProviderGoup(config, providerPath, finalPathChildrenCache.getCurrentData()));
                                break;
                            case CHILD_UPDATED: // 更新一个Provider
                                notifyObserver(toProviderGoup(config, providerPath, finalPathChildrenCache.getCurrentData()));
                                break;
                            default:
                                break;
                        }
                    }
                });
                pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
                INTERFACE_PROVIDER_CACHE.put(config, pathChildrenCache);
            }
            ProviderGroup providerGroup = toProviderGoup(config,providerPath,pathChildrenCache.getCurrentData());
            log.debug("subscribe provider:{}", providerGroup);
            return providerGroup;
        } catch (Exception e) {
            throw new RPCRuntimeException("Failed to subscribe provider from zookeeperRegistry!", e);
        }
    }

    public ProviderGroup toProviderGoup(ConsumerConfig config,String providerPath, List<ChildData> childDataList){
        ProviderGroup providerGroup = new ProviderGroup();
        providerGroup.setServiceName(config.getServiceName());
        List<ProviderInfo> providerInfos = toProviderInfos(config,providerPath, childDataList);
        providerGroup.setProviderInfos(providerInfos);
        return providerGroup;
    }

    @Override
    public void registerObserver(ProviderObserver providerObserver) {
        observers.put(providerObserver.getServiceName(),providerObserver);
    }

    public void notifyObserver(ProviderGroup providerGroup){
        observers.get(providerGroup.getServiceName()).update(providerGroup);
    }



    public List<ProviderInfo> toProviderInfos(ConsumerConfig config,String providerPath, List<ChildData> childDataList) {
        List<ProviderInfo> providerInfos = new ArrayList<>();
        for (ChildData childData : childDataList) {
            String data = new String(childData.getData());
            ProviderInfo providerInfo = JSONUtils.parseObject(data, ProviderInfo.class);
            String url = childData.getPath().substring(providerPath.length() + 1);
            url = UrlUtils.getURLDecoderString(url);
            providerInfo.setUrl(url);
            int protocolIndex = url.indexOf("://");
            String protocol = url.substring(0, protocolIndex);
            providerInfo.setProtocol(protocol);
            String remainPath = url.substring(protocolIndex + 3);
            int addressIndex = remainPath.indexOf('?');
            String address = remainPath.substring(0, addressIndex);
            String[] ipAndPort = address.split(":", -1);
            providerInfo.setHost(ipAndPort[0]);
            providerInfo.setPort(CommonUtils.parseInt(ipAndPort[1],RpcDefaultConfig.PORT));
            Map<String, String> urlParams = UrlUtils.getUrlParams(url);
            providerInfo.setAppName(urlParams.get(RpcConstants.CONFIG_KEY_APP_NAME));
            providerInfos.add(providerInfo);
        }
        return providerInfos;
    }

    /***
     * 订阅
     * @param config
     */
    protected void subscribeConsumerUrls(ConsumerConfig config) {
        // 注册Consumer节点
        String url = null;
        try {
            String consumerPath = RegistryUtils.buildConsumerPath(config.getServiceName());
            url = RegistryUtils.convertConsumerToUrl(config);
            String encodeUrl = URLEncoder.encode(url, "UTF-8");
            getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL) // Consumer临时节点
                    .forPath(consumerPath + CONTEXT_SEP + encodeUrl,config.convertData());

            try {
                NodeCache nodeCache = INTERFACE_CONSUMER_CACHE.get(config);
                if (nodeCache == null) {
                    nodeCache = new NodeCache(zkClient, consumerPath + CONTEXT_SEP + encodeUrl);
                    final NodeCache finalNodeCache = nodeCache;
                    nodeCache.getListenable().addListener(new NodeCacheListener(){

                        @Override
                        public void nodeChanged() throws Exception {
                            // 节点发生变化，回调方法
                            byte[] data = finalNodeCache.getCurrentData().getData();
                            String dataJsonStr = new String(data);
                            // getData()方法实现返回byte[]
                            ConsumerConfig changeConsumerConfig = JSONUtils.parseObject(dataJsonStr, ConsumerConfig.class);
                            config.setInvokeTimeout(changeConsumerConfig.getInvokeTimeout());
                            config.setLoadBlance(changeConsumerConfig.getLoadBlance());
                            config.setSerialization(changeConsumerConfig.getSerialization());
                            config.setRetries(changeConsumerConfig.getRetries());
                            log.info("consumer data change:{}",config);
                        }
                    });
                    //StartMode.BUILD_INITIAL_CACHE同步初始化缓存数据
                    finalNodeCache.start(true);
                    INTERFACE_CONSUMER_CACHE.put(config, nodeCache);
                }
            } catch (Exception e) {
                throw new RPCRuntimeException("Failed to subscribe provider from zookeeperRegistry!", e);
            }

        } catch (KeeperException.NodeExistsException nodeExistsException) {
            if (log.isWarnEnabled()) {
                log.warn("consumer has exists in zookeeper, consumer=" + url);
            }
        } catch (Exception e) {
            throw new RPCRuntimeException("Failed to register consumer to zookeeperRegistry!", e);
        }
    }


}
