package com.bluemethod.jabs.jabs.persistence;

import com.bluemethod.jabs.jabs.model.Token;

public interface TokenRepositoryCustom {
    Token getTokenFromHash(String tokenHash);
}
