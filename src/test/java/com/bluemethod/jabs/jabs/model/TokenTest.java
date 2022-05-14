package com.bluemethod.jabs.jabs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class TokenTest 
{
    private final String TOKEN_HASH = "1234";
    private final int USER_ID = 1;
    private final int DAYS_TILL_EXPIRE = 1;
    private final int HOURS_TILL_EXPIRE = 0;

    private final String TODAY = "01-1-2002 00:00:00";
    private final String EXPIRE = "02-1-2002 01:00:00";

    private Token cut;

    @BeforeEach
    public void setup()
    {
        cut = new Token();
    }

    @Test
    public void testTokenGeneration()
    {
        Token token = new Token(TOKEN_HASH, USER_ID, DAYS_TILL_EXPIRE, HOURS_TILL_EXPIRE);

        assertTrue(token.getTokenHash().equals(TOKEN_HASH));
        assertEquals(token.getUserId(), USER_ID);
        assertTrue(token.get)
    }
}
