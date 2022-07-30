package com.bluemethod.jabs.jabs.controller;

import com.bluemethod.jabs.jabs.model.Token;
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
    @Autowired
    private TokenRepository tokenRepo;

    @DgsQuery
    public User getUserById(@InputArgument Integer id) {
        return userRepo.findById(id).orElse(null);
    }

    @DgsQuery
    public User getUserByUsername(@InputArgument String username) {
        return userRepo.findUserByUsername(username);
    }

    @DgsQuery
    public Token login(@InputArgument String username, @InputArgument String password) {
        return userRepo.login(username, password);
    }

    @DgsQuery
    public User authorize(@InputArgument String token) {
        return userRepo.authorize(token);
    }
}
