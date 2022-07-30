package com.bluemethod.jabs.jabs.controller;

import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.UserRepository;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

@DgsComponent
public class UserFetcher {

    @Autowired
    private UserRepository userRepo;

    @DgsQuery
    public User getUserById(@InputArgument Integer id) {
        return userRepo.findById(id).orElse(null);
    }

    @DgsQuery
    public User getUserByUsername(@InputArgument String username) {
        return userRepo.findUserByUsername(username);
    }

    @DgsQuery
    public User login(@InputArgument String username, @InputArgument String password) {
        return userRepo.login(username, password);
    }
}
