package com.bluemethod.jabs.jabs.persistence;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.custom.TokenRepositoryCustom;
import com.bluemethod.jabs.jabs.persistence.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer>, TokenRepositoryCustom {
    User authorize(String token);
}
