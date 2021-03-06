package com.bluemethod.jabs.jabs.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.bluemethod.jabs.jabs.utils.HTTPRequest;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a User, stored in the table user
 */
@Entity
@Component
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //ID is generated by SQL

    private String username; //Display name for identification
    private String steamID; //If used, the player's SteamID
    private String dateCreated; //The date the user was created
    private String lastLoggedIn; //The date the user last logged in
    
    private boolean isBanned; //Has the user been perma banned
    private String bannedUntil; //Temporary bans

    public User() { }

    /**
     * Generates a new user using a steam id, setting the username
     * to the players steam name
     * @param steamID the steamID
     */
    @SuppressWarnings("unchecked")
    public User(String steamID, String steamKey) {
        this.setSteamID(steamID);

        //Generate new values
        Date today = new Date();
        this.setDateCreated(today.toString());
        this.setLastLoggedIn(today.toString());

        this.setBanned(false);
        this.setBannedUntil("");

        //Fetch the username
        String url = "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/";
        Map<String, String> params = new HashMap<>();

        //TODO: Store the steam key in some enviroment variable
        params.put("key", steamKey);
        params.put("steamids", steamID);

        Map<String, Object> response;
        try {
            response = HTTPRequest.getHTTPRequest(url, params);
            response = (Map<String, Object>) response.get("response");
            List<Object> player = (List<Object>) response.get("players");
            response = (Map<String, Object>) player.get(0);
            this.setUsername((String) response.get("personaname"));
        } catch (IOException e) {
            //If we fail to get the username for whatever reason, set it to something random
            double name = Math.random() * 10000000;
            this.setUsername(Double.toString(name));
        }
    }

    /**
     * Constructor is called if a user is being created from getting
     * read from the database
     */
    public User(String username, String steamID, 
                String dateCreated, String lastLoggedIn, 
                boolean isBanned, String bannedUntil)
    {
        this.setUsername(username);
        this.setSteamID(steamID);
        this.setDateCreated(dateCreated);
        this.setLastLoggedIn(lastLoggedIn);
        this.setBanned(isBanned);
        this.setBannedUntil(bannedUntil);
    }

    /**
     * Determine if the player is banned
     * @return true if the player is banned (temp or perma)
     */
    public boolean playerBanned() {
        return isBanned();
    }

    /**
     * Determine if a player is temporarily banned
     * @return a date for when the ban is lifted, empty string if otherwise
     */
    public String playerTempBanned() {
        if (bannedUntil == "")
            return "";

        Date today = new Date();
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        try {
            Date bannedDate = formatter.parse(getBannedUntil());
            if (today.compareTo(bannedDate) >= 0)
                return "";
        
            return getBannedUntil();
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSteamID() {
        return steamID;
    }

    public void setSteamID(String steamID) {
        this.steamID = steamID;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public String getBannedUntil() {
        return bannedUntil.toString();
    }

    public void setBannedUntil(String bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    @Override
    public String toString()
    {
        return "User{id=" + getId() + ", username=" + getUsername() + ", steamID=" + getSteamID() + "}";
    }
}
