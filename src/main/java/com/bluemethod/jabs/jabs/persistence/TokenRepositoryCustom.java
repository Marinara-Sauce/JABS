package com.bluemethod.jabs.jabs.persistence;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;

public interface TokenRepositoryCustom {
    /**
     * Fetches the token based on a token's hash value
     * @param tokenHash the token's hash (SHA-256)
     * @return the token
     */
    Token getTokenFromHash(String tokenHash);

    /**
     * Authenticates a certain token to it's cooresponding user
     * Useful for being called by other non-user related classes for
     * authentication, such as matchmaking
     * 
     * @param userRepo a reference from the controller for the user repository
     * @param token the token provided by the client
     * @return the user if valid, null otherwise
     */
    User authenticateToken(UserRepository userRepo, String token);
}
