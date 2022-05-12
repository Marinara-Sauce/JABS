package com.bluemethod.jabs.jabs.persistence;

import java.util.List;

import com.bluemethod.jabs.jabs.model.Token;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@Transactional(readOnly = true)
public class TokenRepositoryImpl implements TokenRepositoryCustom
{
    @PersistenceContext
    private EntityManager entity;

    @Override
    public int getUserIdFromToken(String tokenHash) {
        Query query = entity.createNativeQuery(
            "SELECT t.* FROM token as t WHERE t.token_hash LIKE ?"
            , Token.class);
        query.setParameter(1, tokenHash + "%");

        try
        {
            @SuppressWarnings("unchecked")
            List<Token> users = query.getResultList();

            return users.get(0).getUserId();
        }
        catch (Exception e)
        {
            return -1;
        }
    }
}
