package com.bluemethod.jabs.jabs.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for user
 */
@Tag("Model-tier")
public class UserTest {

    private Date today = new Date();
    private SimpleDateFormat dateFormat;

    private final String FUTURE = "22-01-2099 10:15:55 AM";

    private final String TEST_NAME = "TEST_USER";
    private final String TEST_PASSWD = "123";
    private final String TEST_STEAM_ID = "76561197960435530";
    private User cut;

    @BeforeEach
    void setup() throws ParseException
    {
        //Initialize Dates
        today = new Date();
        dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        cut = new User(TEST_NAME, TEST_STEAM_ID, today.toString(), today.toString(), true, FUTURE);
    }

    @Test
    void testUsernameConstructor()
    {
        User user2 = new User(TEST_NAME, TEST_PASSWD);

        assertTrue(user2.getSteamID().isEmpty());
        assertTrue(user2.getUsername().equals(TEST_NAME));
        assertTrue(user2.getPassword().equals(TEST_PASSWD));
        assertTrue(user2.getBannedUntil().isEmpty());
        assertFalse(user2.isBanned());
        assertTrue(user2.getDisplayName().equals(TEST_NAME));
    }

    @Test
    void testSteamIDConstructor()
    {
        User user2 = new User(TEST_STEAM_ID);
        assertTrue(user2.getSteamID().equals(TEST_STEAM_ID));
        assertTrue(user2.getBannedUntil().equals(""));
        assertTrue(user2.getDateCreated().equals(user2.getLastLoggedIn()));
        assertFalse(user2.isBanned());
        assertTrue(user2.getUsername().equals("Robin"));
        assertTrue(user2.getPassword().isEmpty());
    }

    @Test
    void testGetBannedUntil() {
        assertTrue(cut.getBannedUntil().equals(FUTURE));
    }

    @Test
    void testGetDateCreated() {
        assertTrue(cut.getDateCreated().equals(today.toString()));
    }

    @Test
    void testGetId() {
        cut.setId(10);
        assertEquals(cut.getId(), 10);
    }

    @Test
    void testGetLastLoggedIn() {
        assertTrue(cut.getLastLoggedIn().equals(today.toString()));
    }

    @Test
    void testGetSteamID() {
        assertTrue(cut.getSteamID().equals(TEST_STEAM_ID));
    }

    @Test
    void testGetUsername() {
        assertTrue(cut.getUsername().equals(TEST_NAME));
    }

    @Test
    void testIsBanned() {
        assertTrue(cut.isBanned());
    }

    @Test
    void testPlayerBanned() {
        // Test for perma banned
        assertTrue(cut.playerBanned());

        //Ensure that we can be unbanned
        cut.setBanned(false);
        assertFalse(cut.playerBanned());
    }

    @Test
    void testPlayerTempBanned() {
        //The player is temp banned right now, check we get the correct date
        assertTrue(cut.playerTempBanned().equals(FUTURE));

        //Set the ban date to something before today
        cut.setBannedUntil("22-01-1970 10:15:55 AM");

        assertTrue(cut.playerTempBanned().equals(""));

        //Make sure that an empty ban date returns ""
        cut.setBannedUntil("");
        assertTrue(cut.playerTempBanned().equals(""));

        //Thorw an exception and ensure we still get an empty string
        cut.setBannedUntil("alksmdlkasmd");
        assertTrue(cut.playerTempBanned().equals(""));
    }

    @Test
    void testToString() {
        cut.setId(10);
        assertTrue(cut.toString().equals("User{id=10, username=" + TEST_NAME + ", steamID=" + TEST_STEAM_ID + "}"));
    }
}
