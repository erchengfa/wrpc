package com.github.wang.wrpc.admin.common;

import com.github.wang.wrpc.admin.config.ZkProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;


@Slf4j
public class ZKClient {
    /**
     * 客户端
     */
    private CuratorFramework client = null;


    public ZKClient(ZkProperties zkProperties) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFrameworkFactory.Builder zkClientuilder = CuratorFrameworkFactory.builder()
                .connectString(zkProperties.getAddress())
                .sessionTimeoutMs(zkProperties.getConnectionTimeout() * 3)
                .connectionTimeoutMs(zkProperties.getConnectionTimeout())
                .canBeReadOnly(false)
                .retryPolicy(retryPolicy)
                .defaultData(null);
        client = zkClientuilder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (log.isInfoEnabled()) {
                    log.info("reconnect to zookeeper,recover provider and consumer data");
                }
            }
        });
        client.start();
    }

    /**
     * 创建节点
     *
     * @param path       路径
     * @param createMode 节点类型
     * @param data       节点数据
     * @return 是否创建成功
     */
    public boolean crateNode(String path, CreateMode createMode, String data) {
        try {
            client.create().withMode(createMode).forPath(path, data.getBytes());
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);
            return false;
        }

        return true;
    }

    /**
     * 删除节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteNode(String path) {
        try {
            client.delete().forPath(path);
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);
            return false;
        }

        return true;
    }

    /**
     * 删除一个节点，并且递归删除其所有的子节点
     *
     * @param path 路径
     * @return 删除结果
     */
    public boolean deleteChildrenIfNeededNode(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);
            return false;
        }

        return true;
    }

    /**
     * 判断节点是否存在
     *
     * @param path 路径
     * @return true-存在  false-不存在
     */
    public boolean isExistNode(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);

            return stat != null ? true : false;
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);

            return false;
        }
    }

    /**
     * 判断节点是否是持久化节点
     *
     * @param path 路径
     * @return 2-节点不存在  | 1-是持久化 | 0-临时节点
     */
    public int isPersistentNode(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);

            if (stat == null) {
                return 2;
            }

            if (stat.getEphemeralOwner() > 0) {
                return 1;
            }

            return 0;
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);

            return 2;
        }
    }

    /**
     * 获取节点数据
     *
     * @param path 路径
     * @return 节点数据，如果出现异常，返回null
     */
    public String getNodeData(String path) {

        try {
            byte[] bytes = client.getData().forPath(path);
            return new String(bytes);
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);
            return null;
        }
    }

    /**
     * 注册节点数据变化事件
     *
     * @param path              节点路径
     * @param nodeCacheListener 监听事件
     * @return 注册结果
     */
    public boolean registerWatcherNodeChanged(String path, NodeCacheListener nodeCacheListener) {
        NodeCache nodeCache = new NodeCache(client, path, false);
        try {
            nodeCache.getListenable().addListener(nodeCacheListener);

            nodeCache.start(true);
        } catch (Exception e) {
            log.error("zk client registerWatcherNodeChanged error:",e);
            return false;
        }

        return true;
    }


    /**
     * 更新节点数据
     * @param path     路径
     * @param newValue 新的值
     * @return 更新结果
     */
    public boolean updateNodeData(String path, String newValue) {
        //判断节点是否存在
        if (!isExistNode(path)) {
            return false;
        }

        try {
            client.setData().forPath(path, newValue.getBytes());
        } catch (Exception e) {
            log.error("zk client updateNodeData error:",e);
            return false;
        }

        return true;
    }

    /**
     * 获取子节点列表
     * @param path
     * @return
     */
    public List<String> getChildList(String path){
        try {
            List<String> childrenList = client.getChildren().forPath(path);
            return childrenList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

}
