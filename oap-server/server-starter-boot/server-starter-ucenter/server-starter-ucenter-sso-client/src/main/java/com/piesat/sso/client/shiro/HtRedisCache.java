package com.piesat.sso.client.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisCache;
import org.crazycake.shiro.exception.CacheManagerPrincipalIdNotAssignedException;
import org.crazycake.shiro.exception.PrincipalIdNullException;
import org.crazycake.shiro.exception.PrincipalInstanceException;
import org.crazycake.shiro.exception.SerializationException;
import org.crazycake.shiro.serializer.ObjectSerializer;
import org.crazycake.shiro.serializer.RedisSerializer;
import org.crazycake.shiro.serializer.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-18 11:48
 **/
public class HtRedisCache<K, V>  implements Cache<K, V> {
    private static Logger logger = LoggerFactory.getLogger(RedisCache.class);
    private String keyPrefix = "shiro:cache:";
    private HtRedisManager redisManager;
    private int expire = 1800;
    private String principalIdFieldName = "id";

    public HtRedisCache(HtRedisManager redisManager, String prefix, int expire, String principalIdFieldName) {

        if(redisManager == null) {
            throw new IllegalArgumentException("redisManager cannot be null.");
        } else {

            if(prefix != null && !"".equals(prefix)) {
                this.keyPrefix = prefix;
            }
            this.redisManager=redisManager;
            this.expire = expire;
            if(principalIdFieldName != null) {
                this.principalIdFieldName = principalIdFieldName;
            }


        }
    }
    @Override
    public V get(K key) throws CacheException {
        logger.debug("get key [" + key + "]");
        if(key == null) {
            return null;
        } else {
            try {
                String redisCacheKey = this.getRedisCacheKey(key);
                Object rawValue = this.redisManager.get(redisCacheKey);
                if(rawValue == null) {
                    return null;
                } else {
                    return (V) rawValue;
                }
            } catch (Exception var5) {
                throw new CacheException(var5);
            }
        }
    }
    @Override
    public V put(K key, V value) throws CacheException {
        logger.debug("put key [{}]",key);
        if (key == null) {
            logger.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return value;
        }
        try {
            String redisCacheKey = getRedisCacheKey(key);
            this.redisManager.set(redisCacheKey, value != null ? value : null, expire);
            return value;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        logger.debug("remove key [{}]",key);
        if (key == null) {
            return null;
        }
        try {
            String redisCacheKey = getRedisCacheKey(key);
            Object rawValue = redisManager.get(redisCacheKey);
            V previous = (V) rawValue;
            redisManager.del(redisCacheKey);
            return previous;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
    private String getRedisCacheKey(K key) {
        return key == null?null:this.keyPrefix + this.getStringRedisKey(key);
    }

    private String getStringRedisKey(K key) {
        String redisKey;
        if(key instanceof PrincipalCollection) {
            redisKey = this.getRedisKeyFromPrincipalIdField((PrincipalCollection)key);
        } else {
            redisKey = key.toString();
        }

        return redisKey;
    }

    private String getRedisKeyFromPrincipalIdField(PrincipalCollection key) {
        Object principalObject = key.getPrimaryPrincipal();
        Method pincipalIdGetter = this.getPrincipalIdGetter(principalObject);
        return this.getIdObj(principalObject, pincipalIdGetter);
    }

    private String getIdObj(Object principalObject, Method pincipalIdGetter) {
        try {
            Object idObj = pincipalIdGetter.invoke(principalObject, new Object[0]);
            if(idObj == null) {
                throw new PrincipalIdNullException(principalObject.getClass(), this.principalIdFieldName);
            } else {
                String redisKey = idObj.toString();
                return redisKey;
            }
        } catch (IllegalAccessException var5) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName, var5);
        } catch (InvocationTargetException var6) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName, var6);
        }
    }

    private Method getPrincipalIdGetter(Object principalObject) {
        Method pincipalIdGetter = null;
        String principalIdMethodName = this.getPrincipalIdMethodName();

        try {
            pincipalIdGetter = principalObject.getClass().getMethod(principalIdMethodName, new Class[0]);
            return pincipalIdGetter;
        } catch (NoSuchMethodException var5) {
            throw new PrincipalInstanceException(principalObject.getClass(), this.principalIdFieldName);
        }
    }

    private String getPrincipalIdMethodName() {
        if(this.principalIdFieldName != null && !"".equals(this.principalIdFieldName)) {
            return "get" + this.principalIdFieldName.substring(0, 1).toUpperCase() + this.principalIdFieldName.substring(1);
        } else {
            throw new CacheManagerPrincipalIdNotAssignedException();
        }
    }
   @Override
    public void clear() throws CacheException {
       logger.debug("clear cache");
       Set<String> keys = null;
       try {
           keys = redisManager.scan(this.keyPrefix + "*");
       } catch (Exception e) {
           logger.error("get keys error", e);
       }
       if (keys == null || keys.size() == 0) {
           return;
       }
       for (String key : keys) {
           redisManager.del(key);
       }
   }
    @Override
    public int size() {
        Long longSize = 0L;
        try {
            longSize = new Long(redisManager.scanSize(this.keyPrefix + "*"));
        } catch (Exception e) {
            logger.error("get keys error", e);
        }
        return longSize.intValue();
    }
    @Override
    public Set<K> keys() {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            logger.error("get keys error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Set<K> convertedKeys = new HashSet<K>();
        for (String key:keys) {
            try {
                convertedKeys.add((K) key);
            } catch (Exception e) {
                logger.error("deserialize keys error", e);
            }
        }
        return convertedKeys;
    }

    public Collection<V> values() {
        Set<String> keys = null;
        try {
            keys = redisManager.scan(this.keyPrefix + "*");
        } catch (Exception e) {
            logger.error("get values error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> values = new ArrayList<V>(keys.size());
        for (String key : keys) {
            V value = null;
            try {
                value = (V) redisManager.get(key);
            } catch (Exception e) {
                logger.error("deserialize values= error", e);
            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public HtRedisManager getRedisManager() {
        return redisManager;
    }

    public void setRedisManager(HtRedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public String getPrincipalIdFieldName() {
        return principalIdFieldName;
    }

    public void setPrincipalIdFieldName(String principalIdFieldName) {
        this.principalIdFieldName = principalIdFieldName;
    }
}

