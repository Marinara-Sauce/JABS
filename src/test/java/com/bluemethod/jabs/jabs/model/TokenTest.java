package com.bluemethod.jabs.jabs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class TokenTest 
{
    private final String TOKEN_HASH = "1234";
    private final int USER_ID = 1;
    private final String EXPIRATION = "01-01-2099 12:00:00 AM";
    private final int DAYS_TILL_EXPIRE = 1;
    private final int HOURS_TILL_EXPIRE = 1;

    private Token cut;

    @BeforeEach
    public void setup()
    {
        cut = new Token(TOKEN_HASH, USER_ID, EXPIRATION);
    }

    @Test
    public void testTokenGeneration()
    {
        Date today = new Date();

        //Generate test calender for comparison
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, DAYS_TILL_EXPIRE);
        cal.add(Calendar.HOUR_OF_DAY, HOURS_TILL_EXPIRE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String expectedExpiration = sdf.format(cal.getTime());

        Token token = new Token(TOKEN_HASH, USER_ID, DAYS_TILL_EXPIRE, HOURS_TILL_EXPIRE);

        assertTrue(token.getTokenHash().equals(TOKEN_HASH));
        assertEquals(token.getUserId(), USER_ID);
        assertTrue(token.getExpiration().equals(expectedExpiration));
    }

    @Test
    public void testTokenRetrival()
    {
        Token newToken = new Token(TOKEN_HASH, USER_ID, EXPIRATION);
        assertTrue(newToken.getTokenHash().equals(TOKEN_HASH));
        assertEquals(newToken.getUserId(), USER_ID);
        assertTrue(newToken.getExpiration().equals(EXPIRATION));
    }

    @Test
    public void testIsExpired()
    {
        assertFalse(cut.isExpired());
        cut.setExpiration("01-01-2002 12:00:00 AM");
        assertTrue(cut.isExpired());
        cut.setExpiration("this will cause an error");
        assertTrue(cut.isExpired());
    }

    @Test
    void testSetId() {
        cut.setId(2);
        assertEquals(cut.getId(), 2);
    }
}
