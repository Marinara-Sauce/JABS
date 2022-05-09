package com.bluemethod.jabs.jabs.persistence;

import java.util.List;

import com.bluemethod.jabs.jabs.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepositoryCustom {
    
    @PersistenceContext
    EntityManager entity;

    /**
     * Searches the DB for users matching the username
     * @param username the username to search for
     * @return the list of users with that username
     */
    @Override
    public List<User> findUsersByUsername(String username)
    {
        Query query = entity.createNativeQuery("SELECT us.* FROM user as us WHERE us.username LIKE ?", User.class);
        query.setParameter(1, username + "%");

        try
        {
            @SuppressWarnings("unchecked")
            List<User> users = query.getResultList();

            return users;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
