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
        Query query = entity.createNativeQuery(
            "SELECT us.* FROM user as us WHERE us.username LIKE ?"
            , User.class);
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

    /**
     * Searches the DB for users with a certain steam ID
     * @param id the id to search for
     * @return the user with that steam id
     */
    @Override
    public User findUserBySteamId(String id)
    {
        Query query = entity.createNativeQuery(
            "SELECT us.* FROM user as us WHERE us.steamid LIKE ?",
            User.class
        );
        query.setParameter(1, id);

        try
        {
            if (query.getResultList().size() == 0)
                return null;
            
            else if (query.getResultList().size() == 1)
            {
                User u = (User) query.getResultList().get(0);
                return u;
            }

            else
            {
                System.err.println("2 Users have the same ID: " + id);
                return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }
}
