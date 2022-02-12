package com.starter.app.ws.starterdemo.ui.controller;

import com.starter.app.ws.starterdemo.service.UserService;
import com.starter.app.ws.starterdemo.ui.model.request.UpdateUserRequest;
import com.starter.app.ws.starterdemo.ui.model.request.UserRequest;
import com.starter.app.ws.starterdemo.ui.model.response.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// had to import spring.boot.starter.validation
// also had to add the following to the application.properties file
// server.error.include-message=always
// server.error.include-binding-errors=always
// refer https://www.appsdeveloperblog.com/validate-request-body-in-restful-web-service/
// refer https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-defineconstraints-spec
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

    // This is to store in memory when the session is running
    // This imitates the actual database system
    private Map<String, User> users;

    @Autowired
    UserService userService;

    // for query parameter example
    // to make a field optional -
    // if it is a primitive variable, pass the defaultValue
    // if it is an object, required = false
    @GetMapping
    public String getUserWithQueryParams(
            @RequestParam(value="page") int page,
            @RequestParam(value="limit", defaultValue = "1") int limit,
            @RequestParam(value="object", required=false) String something
    ) {
        return "getUserWithQueryParams was called with page  = " + page + " and limit = " + limit + " and something is " + something;
    }

    // the produces here means any client that wants to accept the response in json or xml can do so
    // All they have to do is set the Accept = application/xml in headers
    // also added jackson dataformat xml dependency
    @GetMapping(path = "/{userId}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public User getUser(@PathVariable String userId) {
        final String str = null;
        final int n = str.length();
        return new User("Mitul", "Sheth", "mitulsadasda", userId);
    }

    // the produces here means any client that wants to accept the response in json or xml can do so
    // All they have to do is set the Accept = application/xml in headers
    // also added jackson dataformat xml dependency
    @GetMapping(path = "/{userId}/entity", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> getUserResponseEntity(@PathVariable String userId) {
        final User user = new User("Mitul", "Sheth", "mitulsadasda", "empty user");
        if (users.containsKey(userId)) {
            return new ResponseEntity<>(users.get(userId), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // @RequestBody to send the body, consumes determines what tyoe of data type can come in
    // @Valid is for setting up validation on the UserRequest Bean. If this annotation is not present,
    // then the validations on the bean do not take place. If more than two validations fail, we receive messages
    // for both errors.
    @PostMapping(consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest userRequest) {
        final User user = userService.createUser(userRequest);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping(path = "/{userId}", consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<User> updateUser(@PathVariable final String userId, @Valid @RequestBody final UpdateUserRequest updateUserRequest) {
        User user = users.get(userId);
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable final String userId) {
        if (users != null) {
            users.remove(userId);
        }
        return ResponseEntity.noContent().build();
    }
}
