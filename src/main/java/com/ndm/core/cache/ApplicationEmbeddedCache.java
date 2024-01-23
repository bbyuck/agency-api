package com.ndm.core.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationEmbeddedCache implements Cache {

    private final Long SEVEN_DAYS = 10080000L;

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    private final Map<String, Long> ttl = new ConcurrentHashMap<>();

    @Override
    public Object get(String key) {
        if (ttl.get(key) == null) {
            return null;
        }
        // timeout
        if (ttl.get(key) <= System.currentTimeMillis()) {
            store.remove(key);
            ttl.remove(key);
            return null;
        }

        return store.get(key);
    }

    @Override
    public void add(String key, Object value) {
        store.remove(key);
        ttl.remove(key);

        store.put(key, value);
        ttl.put(key, System.currentTimeMillis() + SEVEN_DAYS);
    }
}
