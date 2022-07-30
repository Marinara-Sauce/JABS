package com.bluemethod.jabs.jabs.persistence.custom;

import com.bluemethod.jabs.jabs.model.Token;

public interface TokenRepositoryCustom {
    Token authorize(String token);
}
