package com.ndm.core.cache;

public interface Cache {
    Object get(String key);

    void add(String key, Object value);
}
