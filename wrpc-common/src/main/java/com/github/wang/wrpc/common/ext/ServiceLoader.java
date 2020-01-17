package com.github.wang.wrpc.common.ext;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author : wang
 * @date : 2020/1/8
 */
@Slf4j
public class ServiceLoader<T> {

    private static final String[] paths = {"META-INF/services/wrpc/"};

    protected final Class<T> interfaceClass;

    protected final String interfaceName;

    protected final ConcurrentMap<String, SpiClassInfo<T>> spiClassInfoMap;

    protected final ConcurrentMap<String, T> singleInstanceMap;


    private Spi spi;

    protected ServiceLoader(Class<T> interfaceClass) {
        if (interfaceClass == null ||
                !(interfaceClass.isInterface() || Modifier.isAbstract(interfaceClass.getModifiers()))) {
            throw new IllegalArgumentException("Spi classes must be interface and abstract");
        }
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceClass.getName();

        Spi spi = interfaceClass.getAnnotation(Spi.class);
        if (spi == null) {
            throw new IllegalArgumentException(
                    "This interface does not exist at @spi :" + interfaceName);
        } else {
            this.spi = spi;
        }

        this.singleInstanceMap = spi.singleton() ? new ConcurrentHashMap<String, T>() : null;
        this.spiClassInfoMap = new ConcurrentHashMap<String, SpiClassInfo<T>>();

        for (String path : paths) {
            loadFromFile(path);
        }

    }

    private void loadFromFile(String path) {
        String fullFileName = path + interfaceName;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            loadFromClassLoader(classLoader, fullFileName);
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("");
            }
        }
    }

    private void loadFromClassLoader(ClassLoader classLoader, String fullFileName) throws Throwable{

        Enumeration<URL> urls = classLoader != null ? classLoader.getResources(fullFileName)
                : ClassLoader.getSystemResources(fullFileName);
        // 可能存在多个文件。
        if (urls != null) {
            while (urls.hasMoreElements()) {
                // 读取一个文件
                URL url = urls.nextElement();
                if (log.isDebugEnabled()) {
                    log.debug("Loading extension of extensible {} from classloader: {} and file: {}",
                            interfaceName, classLoader, url);
                }
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        readClass(url, line,classLoader);
                    }
                } catch (Throwable t) {
                    if (log.isDebugEnabled()) {
                        log.debug("Failed to load extension of extensible " + interfaceName
                                + " from classloader: " + classLoader + " and file:" + url, t);
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        }

    }

    private void readClass(URL url, String line,ClassLoader classLoader) throws Throwable{
        String[] aliasAndClassName = parseAliasAndClassName(line);
        log.debug("read class:{}",aliasAndClassName[1]);
        String alias = aliasAndClassName[0];
        String className = aliasAndClassName[1];
        Class aClass = Class.forName(className, false, classLoader);
        SpiClassInfo<T> spiClassInfo = new SpiClassInfo<T>(aClass);
        spiClassInfo.setSingleton(spi.singleton());
        spiClassInfoMap.putIfAbsent(alias,spiClassInfo);
    }


    protected String[] parseAliasAndClassName(String line) {
        if (StringUtils.isBlank(line)) {
            return null;
        }
        line = line.trim();
        int i0 = line.indexOf('#');
        if (i0 == 0 || line.length() == 0) {
            return null; // 当前行是注释 或者 空
        }
        if (i0 > 0) {
            line = line.substring(0, i0).trim();
        }

        String[] aliasAndClassName = line.split("=");
        if (aliasAndClassName.length != 2){
            throw new IllegalArgumentException("Spi parsing line error:" + line);
        }
        return aliasAndClassName;
    }


    public T getInstance(String alias) {
        SpiClassInfo<T> SpiClassInfo = getSpiClassInfo(alias);
        if (SpiClassInfo == null) {
            throw new RPCRuntimeException("Not found extension of " + interfaceName + " named: \"" + alias + "\"!");
        } else {
            if (spi.singleton() && singleInstanceMap != null) {
                T t = singleInstanceMap.get(alias);
                if (t == null) {
                    synchronized (this) {
                        t = singleInstanceMap.get(alias);
                        if (t == null) {
                            t = SpiClassInfo.getSpiInstance();
                            singleInstanceMap.put(alias, t);
                        }
                    }
                }
                return t;
            } else {
                return SpiClassInfo.getSpiInstance();
            }
        }
    }

    private SpiClassInfo<T> getSpiClassInfo(String alias) {
        return spiClassInfoMap == null ? null : spiClassInfoMap.get(alias);
    }

    public T getInstance(String alias, Class[] argTypes, Object[] args) {
        SpiClassInfo<T> SpiClassInfo = getSpiClassInfo(alias);
        if (SpiClassInfo == null) {
            throw new RPCRuntimeException("Not found extension of " + interfaceName + " named: \"" + alias + "\"!");
        } else {
            if (spi.singleton() && singleInstanceMap != null) {
                T t = singleInstanceMap.get(alias);
                if (t == null) {
                    synchronized (this) {
                        t = singleInstanceMap.get(alias);
                        if (t == null) {
                            t = SpiClassInfo.getSpiInstance(argTypes, args);
                            singleInstanceMap.put(alias, t);
                        }
                    }
                }
                return t;
            } else {
                return SpiClassInfo.getSpiInstance(argTypes, args);
            }
        }
    }

    List<SpiClassInfo<T>> getAllSpiClassInfo(){
        return (List)spiClassInfoMap.values();
    }


}
