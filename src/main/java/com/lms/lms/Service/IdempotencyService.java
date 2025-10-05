package com.lms.lms.Service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class IdempotencyService {
    
    private final ConcurrentMap<String, String> store = new ConcurrentHashMap<>();
    
    public boolean exists(String key) {
        return store.containsKey(key);
    }
    
    public String get(String key) {
        return store.get(key);
    }
    
    public void put(String key, String resourceId) {
        store.put(key, resourceId);
    }
}
