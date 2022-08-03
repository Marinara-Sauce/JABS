package com.bluemethod.jabs.jabs.controller;

import com.bluemethod.jabs.jabs.exceptions.InvalidTokenException;
import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.TokenRepository;
import com.bluemethod.jabs.jabs.persistence.UserRepository;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
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
    public String login(@InputArgument String username, @InputArgument String password) {
        User u = userRepo.login(username, password);

        if (u != null) {
            String token = Token.generateToken();
            //TODO: Pull these expiration values from a config
            Token newToken = new Token(token, u.getId(), 1, 0);
            tokenRepo.save(newToken);
            return token;
        }

        return null;
    }

    @DgsQuery
    public User authorize(@InputArgument String token) throws InvalidTokenException {
        Token t = tokenRepo.authorize(token);

        if (t == null) {
            throw new InvalidTokenException("Invalid Token");
        }

        if (t.isExpired()) {
            tokenRepo.delete(t);
            throw new InvalidTokenException("Expired Token");
        }

        return userRepo.findById(t.getUserId()).orElse(null);
    }
}
