package com.starter.app.ws.starterdemo.ratelimiter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RateLimiter {

    private final LoadingCache<String, Bucket> cache;

    public RateLimiter(final long tokens, final long timeInSeconds) {
        cache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build(
                new CacheLoader<>() {
                    @Override
                    public Bucket load(String key) {
                        return Bucket.builder()
                                .addLimit(Bandwidth.classic(5, Refill.intervally(tokens, Duration.ofSeconds(timeInSeconds))))
                                .build();
                    }
                }
        );
    }

    public boolean tryAcquire(final String key, final int weight) {
        final Bucket bucket = cache.getUnchecked(key);
        return bucket.tryConsume(weight);
    }

    public void refresh(final String key) {
        cache.refresh(key);
    }

    public long getAvailableTokens(final String key) {
        return cache.getUnchecked(key).getAvailableTokens();
    }

    public Map<String, Bucket> getLocalCache() {
        return cache.asMap();
    }
}
