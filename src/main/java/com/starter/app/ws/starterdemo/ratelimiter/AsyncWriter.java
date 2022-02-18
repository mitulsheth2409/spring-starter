package com.starter.app.ws.starterdemo.ratelimiter;

import com.google.common.cache.LoadingCache;
import com.starter.app.ws.starterdemo.serviceimpl.UserServiceImpl;

public class AsyncWriter implements Runnable {
    private final LoadingCache<String, UserServiceImpl.AdditionalLimit> limitCache;
    private final RateLimiter rateLimiter;

    public AsyncWriter(
            final RateLimiter rateLimiter,
            final LoadingCache<String, UserServiceImpl.AdditionalLimit> limitCache
    ) {
        this.rateLimiter = rateLimiter;
        this.limitCache = limitCache;
    }

    @Override
    public void run() {
        rateLimiter.getLocalCache().entrySet().parallelStream().forEach(entry -> {
            entry.getValue().getAvailableTokens();
        });
        System.out.println(limitCache.asMap().keySet());
        System.out.println("Executed every 10 seconds");
    }
}
