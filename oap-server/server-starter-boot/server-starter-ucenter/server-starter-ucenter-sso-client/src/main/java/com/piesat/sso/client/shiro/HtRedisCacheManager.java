package com.piesat.sso.client.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCache;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.serializer.ObjectSerializer;
import org.crazycake.shiro.serializer.RedisSerializer;
import org.crazycake.shiro.serializer.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-18 12:50
 **/
public class HtRedisCacheManager implements CacheManager {
    private final Logger logger = LoggerFactory.getLogger(RedisCacheManager.class);
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap();
    private HtRedisManager htRedisManager;
    public static final int DEFAULT_EXPIRE = 1800;
    private int expire = 1800;
    public static final String DEFAULT_CACHE_KEY_PREFIX = "shiro:cache:";
    private String keyPrefix = "shiro:cache:";
    public static final String DEFAULT_PRINCIPAL_ID_FIELD_NAME = "id";
    private String principalIdFieldName = "id";

    public HtRedisCacheManager() {
    }
    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        this.logger.debug("get cache, name=" + name);
        Cache cache = (Cache)this.caches.get(name);
        if(cache == null) {
            cache = new HtRedisCache(this.htRedisManager,this.keyPrefix + name + ":", this.expire, this.principalIdFieldName);
            this.caches.put(name, cache);
        }

        return (Cache)cache;
    }


    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }


    public HtRedisManager getHtRedisManager() {
        return htRedisManager;
    }

    public void setHtRedisManager(HtRedisManager htRedisManager) {
        this.htRedisManager = htRedisManager;
    }

    public int getExpire() {
        return this.expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getPrincipalIdFieldName() {
        return this.principalIdFieldName;
    }

    public void setPrincipalIdFieldName(String principalIdFieldName) {
        this.principalIdFieldName = principalIdFieldName;
    }
}

