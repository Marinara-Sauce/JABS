package com.bluemethod.jabs.jabs.persistence.custom;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;

public interface UserRepositoryCustom {
    User findUserByUsername(String username);

    User login(String username, String password);
}
