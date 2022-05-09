package com.bluemethod.jabs.jabs.persistence;

import java.util.List;

import com.bluemethod.jabs.jabs.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    List<User> findUsersByUsername(String username);
}
