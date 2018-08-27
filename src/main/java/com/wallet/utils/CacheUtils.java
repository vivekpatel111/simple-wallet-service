package com.wallet.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheUtils<K, V>
{
    protected static final Logger LOGGER =
        LoggerFactory.getLogger(CacheUtils.class);
    
    private Map<K, Pair<V, Long>> cache;
    private Long ttlInMs;
    
    public CacheUtils(Long ttlInMs)
    {
        this.cache = new HashMap<>();
        this.ttlInMs = ttlInMs;
        
        LOGGER.info("Cache initilized with ttlInMs - " + ttlInMs);
    }
    
    public synchronized void put(K key, V value)
    {
        LOGGER.debug(
            String.format("Adding key<%s> to cache with value - %s", 
                          key, value)
        );
        cache.put(key, new Pair<V, Long>(value, System.currentTimeMillis()));
    }
    
    public synchronized V get(K key)
    {
        LOGGER.debug("Fetching from cache with key - " + key);
        V value = null;
        
        if (cache.containsKey(key))
        {
            LOGGER.debug(
                String.format("Cache hit for key - <%s> with value - %s", 
                               key, value)
            );
            Pair<V, Long> pair = cache.get(key);
            Long currentTime = System.currentTimeMillis();
            
            if (currentTime > pair.getRight() + ttlInMs)
            {
                LOGGER.debug("Removing key from cache - " + key);
                cache.remove(key);
            }
            else
            {
                value = pair.getLeft();
            }
        }
        
        return value;
    }
}
