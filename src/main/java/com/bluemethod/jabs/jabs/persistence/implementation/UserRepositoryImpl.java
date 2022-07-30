package com.bluemethod.jabs.jabs.persistence.implementation;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.custom.UserRepositoryCustom;
import com.bluemethod.jabs.jabs.utils.Crypto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    EntityManager entity;

    @Override
    public User findUserByUsername(String username) {
           Query q = entity.createNativeQuery(
                   "SELECT us.* FROM user as us WHERE us.username LIKE ?"
           , User.class);
           q.setParameter(1, username + "%");

           try {
               @SuppressWarnings("unchecked")
               List<User> users = q.getResultList();

               return users.get(0);
           } catch (Exception e) {
               return null;
           }
    }

    /**
     * Logs in based on a username and password, generates a new
     * token upon login, returning that token
     *
     * That token is then used for any user-specific actions
     */
    @Override
    public User login(String username, String password) {
        User target = findUserByUsername(username);

        if (target.getPasswordHash().equals(Crypto.sha256Hash(password))) {
            return target;
        }

        return null;
    }
}
