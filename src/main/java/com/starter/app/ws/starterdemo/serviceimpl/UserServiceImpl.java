package com.starter.app.ws.starterdemo.serviceimpl;

import com.starter.app.ws.starterdemo.service.UserService;
import com.starter.app.ws.starterdemo.shared.Utils;
import com.starter.app.ws.starterdemo.ui.model.request.UserRequest;
import com.starter.app.ws.starterdemo.ui.model.response.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    // This is to store in memory when the session is running
    // This imitates the actual database system
    private Map<String, User> users;
    private Utils utils;

    public UserServiceImpl() {
    }

    @Autowired
    public UserServiceImpl(final Utils utils) {
        System.out.println("Initialized testing map");
        this.utils = utils;
    }

    @Override
    public User createUser(UserRequest userRequest) {
        String userId = UUID.randomUUID().toString();
        final User user = new User(userRequest.getFirstName(), userRequest.getLastName(), userRequest.getEmail(), userId);

        if (users == null) {
            users = new HashMap<>();
        }
        users.put(userId, user);
        return user;
    }
}
