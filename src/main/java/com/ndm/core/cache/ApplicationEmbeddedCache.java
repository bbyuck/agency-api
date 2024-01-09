package com.ndm.core.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationEmbeddedCache implements Cache {

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public Object get(String key) {
        return store.get(key);
    }

    @Override
    public void add(String key, Object value) {
        store.put(key, value);
    }
}
