package com.bluemethod.jabs.jabs.persistence;

import com.bluemethod.jabs.jabs.model.Token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer>, TokenRepositoryCustom
{
    
}
