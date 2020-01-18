package com.github.wang.registry.zk;


import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.CommonUtils;
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
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
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
import java.util.concurrent.CopyOnWriteArrayList;

import static com.github.wang.wrpc.common.utils.StringUtils.CONTEXT_SEP;


/**
 * @author : wang
 * @date : 2020/1/6
 */
@Slf4j
public class ZookeeperRegistry extends Registry {

    /**
     * 保存服务发布者的url
     */
    private ConcurrentMap<ProviderConfig, List<String>> providerUrls = new ConcurrentHashMap<ProviderConfig, List<String>>();


    /**
     * 保存服务消费者的url
     */
    private ConcurrentMap<ConsumerConfig, String> consumerUrls = new ConcurrentHashMap<ConsumerConfig, String>();


    /**
     * 接口配置{ConsumerConfig：PathChildrenCache} <br>
     * 例如：{ConsumerConfig ： PathChildrenCache }
     */
    private static final ConcurrentMap<ConsumerConfig, PathChildrenCache> INTERFACE_PROVIDER_CACHE = new ConcurrentHashMap<ConsumerConfig, PathChildrenCache>();


    /**
     * Zookeeper zkClient
     */
    private CuratorFramework zkClient;

    private CopyOnWriteArrayList<ProviderObserver> observers = new CopyOnWriteArrayList<>();

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

        String providerPath = RegistryUtils.buildProviderPath(config.getInterfaceName());
        List<String> urls = RegistryUtils.convertProviderToUrls(config);
        try {
            for (String url : urls) {
                String providerUrl = providerPath + CONTEXT_SEP + UrlUtils.getURLEncoderString(url);
                log.debug("register providerUrl:{}", providerUrl);
                getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.EPHEMERAL) // 是否永久节点
                        .forPath(providerUrl, new byte[]{1}); //
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
            final String providerPath = RegistryUtils.buildProviderPath(config.getInterfaceName());
            PathChildrenCache pathChildrenCache = INTERFACE_PROVIDER_CACHE.get(config);
            if (pathChildrenCache == null) {
                // 监听配置节点下 子节点增加、子节点删除、子节点Data修改事件
                // TODO 换成监听父节点变化（只是监听变化了，而不通知变化了什么，然后客户端自己来拉数据的）
                pathChildrenCache = new PathChildrenCache(zkClient, providerPath, true);
                final PathChildrenCache finalPathChildrenCache = pathChildrenCache;
                pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        if (log.isDebugEnabled()) {
                            log.debug("Receive zookeeper event: {},{}", config.getApplicationName());
                        }
                        switch (event.getType()) {
                            case CHILD_ADDED: //加了一个provider
                                ProviderGroup addProviderGroup = toProviderGoup(config, providerPath, finalPathChildrenCache.getCurrentData());
                                notifyObserver(addProviderGroup);
                                break;
                            case CHILD_REMOVED: //删了一个provider
                                ProviderGroup removeProviderGroup = toProviderGoup(config, providerPath, finalPathChildrenCache.getCurrentData());
                                notifyObserver(removeProviderGroup);
                                break;
                            case CHILD_UPDATED: // 更新一个Provider
                                ProviderGroup updateProviderGroup = toProviderGoup(config, providerPath, finalPathChildrenCache.getCurrentData());
                                notifyObserver(updateProviderGroup);
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
        providerGroup.setInterfaceName(config.getInterfaceName());
        List<ProviderInfo> providerInfos = toProviderInfos(config,providerPath, childDataList);
        providerGroup.setProviderInfos(providerInfos);
        return providerGroup;
    }

    @Override
    public void registerObserver(ProviderObserver providerObserver) {
        observers.add(providerObserver);
    }

    public void notifyObserver(ProviderGroup providerGroup){
        for (ProviderObserver providerObserver: observers){
            providerObserver.update(providerGroup);
        }
    }



    public List<ProviderInfo> toProviderInfos(ConsumerConfig config,String providerPath, List<ChildData> childDataList) {
        List<ProviderInfo> providerInfos = new ArrayList<>();
        for (ChildData childData : childDataList) {
            ProviderInfo providerInfo = new ProviderInfo();
            String url = childData.getPath().substring(providerPath.length() + 1);
            url = UrlUtils.getURLDecoderString(url);
            providerInfo.setOriginUrl(url);
            int protocolIndex = url.indexOf("://");
            String protocol = url.substring(0, protocolIndex);
            providerInfo.setProtocol(protocol);
            String remainUrl = url.substring(protocolIndex + 3);
            int addressIndex = remainUrl.indexOf('?');
            String address = remainUrl.substring(0, addressIndex);
            String[] ipAndPort = address.split(":", -1);
            providerInfo.setHost(ipAndPort[0]);
            providerInfo.setPort(CommonUtils.parseInt(ipAndPort[1],RpcDefaultConfig.PORT));

            Map<String, String> urlParams = UrlUtils.getUrlParams(url);
            providerInfo.setWeight(CommonUtils.parseInt(//
                    urlParams.get(RpcConstants.CONFIG_KEY_WEIGHT),//
                    RpcDefaultConfig.PROVIDER_WEIGHT));
            providerInfo.setSerialization(config.getSerialization());
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
            String consumerPath = RegistryUtils.buildConsumerPath(config.getInterfaceName());
            if (consumerUrls.containsKey(config)) {
                url = consumerUrls.get(config);
            } else {
                url = RegistryUtils.convertConsumerToUrl(config);
                consumerUrls.put(config, url);
            }
            String encodeUrl = URLEncoder.encode(url, "UTF-8");
            getAndCheckZkClient().create().creatingParentContainersIfNeeded()
                    .withMode(CreateMode.EPHEMERAL) // Consumer临时节点
                    .forPath(consumerPath + CONTEXT_SEP + encodeUrl);

        } catch (KeeperException.NodeExistsException nodeExistsException) {
            if (log.isWarnEnabled()) {
                log.warn("consumer has exists in zookeeper, consumer=" + url);
            }
        } catch (Exception e) {
            throw new RPCRuntimeException("Failed to register consumer to zookeeperRegistry!", e);
        }
    }


}
