package com.bluemethod.jabs.jabs.persistence.implementation;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.persistence.custom.TokenRepositoryCustom;
import com.bluemethod.jabs.jabs.utils.Crypto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class TokenRepositoryImpl implements TokenRepositoryCustom {

    @PersistenceContext
    EntityManager entity;

    private Token getTokenByHash(String hash) {
        Query q = entity.createNativeQuery(
                "SELECT us.* FROM token as us WHERE us.token_hash LIKE ?"
                , Token.class);
        q.setParameter(1, hash + "%");

        try {
            @SuppressWarnings("unchecked")
            List<Token> tokens = q.getResultList();

            return tokens.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Token authorize(String token) {
        return getTokenByHash(Crypto.sha256Hash(token));
    }

}
