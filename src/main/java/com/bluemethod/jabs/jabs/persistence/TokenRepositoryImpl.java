package com.bluemethod.jabs.jabs.persistence;

import java.util.List;
import java.util.Optional;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.utils.Hashing;

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
    public Token getTokenFromHash(String tokenHash) {
        Query query = entity.createNativeQuery(
            "SELECT t.* FROM token as t WHERE t.token_hash LIKE ?"
            , Token.class);
        query.setParameter(1, tokenHash + "%");

        try
        {
            @SuppressWarnings("unchecked")
            List<Token> token = query.getResultList();

            return token.get(0);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    public User authenticateToken(UserRepository userRepo, String token) {
        //Fetch the token via the hash value
        byte[] tokenHash = Hashing.getSHA(token);
        String tokenStr = Hashing.toHexString(tokenHash);

        Token t = getTokenFromHash(tokenStr);

        if (t == null)
            return null;
        

        Optional<User> u = userRepo.findById(t.getUserId());

        if (!u.isPresent())
            return null;
        
        return u.get();
    }
}
