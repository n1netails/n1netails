package com.n1ne.n1netails.api.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class LoginAttemptService {

    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENTS = 1;

    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(15, MINUTES).maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPT_INCREMENTS + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
