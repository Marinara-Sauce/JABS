package com.bluemethod.jabs.jabs.persistence;

public interface TokenRepositoryCustom {
    int getUserIdFromToken(String tokenHash);
}
