package com.bluemethod.jabs.jabs.persistence.implementation;

import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.custom.TokenRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TokenRepositoryImpl implements TokenRepositoryCustom {

    @PersistenceContext
    EntityManager entity;

    private Token getTokenByHash(String hash) {
        
    }

    public User authorize(String token) {

    }

}
