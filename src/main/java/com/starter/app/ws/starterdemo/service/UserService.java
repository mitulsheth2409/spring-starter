package com.starter.app.ws.starterdemo.service;

import com.starter.app.ws.starterdemo.ui.model.request.UserRequest;
import com.starter.app.ws.starterdemo.ui.model.response.User;

import java.util.Map;

public interface UserService {
    // This is to store in memory when the session is running
    // This imitates the actual database system
    User createUser(UserRequest userRequest);
}
