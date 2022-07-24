package com.bluemethod.jabs.jabs.persistence.custom;

import com.bluemethod.jabs.jabs.model.User;

public interface UserRepositoryCustom {
    User findUserByUsername(String username);
}
