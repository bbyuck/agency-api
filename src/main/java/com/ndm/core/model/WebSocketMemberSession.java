package com.ndm.core.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketMemberSession {

    private Map<String, String> data = new ConcurrentHashMap<>();

    public String getSessionId(String credentialToken) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue().equals(credentialToken)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public String get(String key) {
        return data.get(key);
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public void remove(String key) {
        data.remove(key);
    }
}
