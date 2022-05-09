package com.bluemethod.jabs.jabs.persistence;

import java.util.List;

import com.bluemethod.jabs.jabs.model.User;

public interface UserRepositoryCustom {
    List<User> findUsersByUsername(String username);
}
