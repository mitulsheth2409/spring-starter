package com.starter.app.ws.starterdemo.serviceimpl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.starter.app.ws.starterdemo.ratelimiter.AsyncWriter;
import com.starter.app.ws.starterdemo.ratelimiter.RateLimiter;
import com.starter.app.ws.starterdemo.service.UserService;
import com.starter.app.ws.starterdemo.shared.Utils;
import com.starter.app.ws.starterdemo.ui.model.request.UserRequest;
import com.starter.app.ws.starterdemo.ui.model.response.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    private static final long MAX_REQUESTS_PER_FIVE_MINUTES = 5;
    private static final long FIVE_MINUTES = 30L;
    private static final long EXPIRY_TIME_IN_SECONDS = 30L;
    // This is to store in memory when the session is running
    // This imitates the actual database system
    private Map<String, User> users;
    private final Utils utils;
    private final RateLimiter rateLimiter;
    private final LoadingCache<String, AdditionalLimit> limitCache;

    @Autowired
    public UserServiceImpl(final Utils utils) {
        this.utils = utils;
        this.rateLimiter = new RateLimiter(MAX_REQUESTS_PER_FIVE_MINUTES, FIVE_MINUTES);
        limitCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRY_TIME_IN_SECONDS, TimeUnit.SECONDS).build(
                new CacheLoader<>() {
                    @Override
                    public AdditionalLimit load(String key) {
                        return new AdditionalLimit();
                    }
                }
        );
        final AsyncWriter asyncWriter = new AsyncWriter(rateLimiter, limitCache);
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(asyncWriter, 1, 3, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down executor");
            executor.shutdownNow();
            try {
                final boolean shutdownComplete = executor.awaitTermination(5, TimeUnit.SECONDS);
                System.out.println("executor is shutdown: " + shutdownComplete);
            } catch (InterruptedException e) {
                System.out.println("Could not shutdown executor");
            }
        }));
    }

    @Override
    public User createUser(UserRequest userRequest) {
        final String key = userRequest.getFirstName();
        final AdditionalLimit additionalLimit = limitCache.getUnchecked(key);
        if (additionalLimit.isLimitPostive()) {
            System.out.println("Since limit is positive requests will be allowed");
            additionalLimit.decr();
        } else if (additionalLimit.isLimitReached()) {
            System.out.println();
            System.out.println("Redis requests limit reached");
            System.out.println("No more requests can be served");
            throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);
        } else if (!rateLimiter.tryAcquire(key, 1)) {
            System.out.println("Available tokens are done, switching to redis");
            additionalLimit.setCount(4);
        };
        System.out.println("Available tokens now: " + rateLimiter.getAvailableTokens(key));
        String userId = utils.generateUserId();
        final User user = new User(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userId);

        if (users == null) {
            users = new HashMap<>();
        }
        users.put(userId, user);
        return user;
    }

    public static class AdditionalLimit {
        private int count = -1;
        private int initialCount = -1;

        public boolean isLimitReached() {
            return count == 0;
        }

        public boolean isLimitPostive() {
            return count > 0;
        }

        public void decr() {
            System.out.println("Allowing sixth request");
            count--;
        }

        public void setCount(int count) {
            this.initialCount = count;
            this.count = count;
        }

        public int getInitialLimit() {
            return initialCount;
        }
    }
}
