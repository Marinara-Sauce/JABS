package com.bluemethod.jabs.jabs.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * The model class for login tokens
 */
@Entity
@Component
public class Token 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String tokenHash; //token hash
    private int userId; //cooresponding user id
    private String expirationDate; // Date of the token's expiration    

    public Token() { }

    /**
     * Generate a new Token without an expiration date.
     * An expiration date will be formatted for the user
     * 
     * @param tokenHash the token's hash
     * @param userId the cooresponding user id
     * @param daysTillExpire how many days the token is valid
     * @param hoursTillExpire how many hours the token is valid
     */
    public Token(String tokenHash, int userId, int daysTillExpire, int hoursTillExpire) {
        this.tokenHash = tokenHash;
        this.userId = userId;

        //Generate an expiration date
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, daysTillExpire);
        cal.add(Calendar.HOUR_OF_DAY, hoursTillExpire);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        this.expirationDate = sdf.format(cal.getTime());
    }

    /**
     * Generates a new token with a known expiration date
     * 
     * @param tokenHash the token's hash
     * @param userId the cooresponding user id
     * @param expiration the expiration date
     */
    public Token(String tokenHash, int userId, String expiration) {
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expirationDate = expiration;
    }

    /**
     * Determines if a token is expired
     * @return true if expired, false if still valid
     */
    public boolean isExpired() {
        //Convert the string date to an actual date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        Date exp;

        try {
            exp = sdf.parse(expirationDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        
        Date today = new Date();
        return today.compareTo(exp) > 0;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTokenHash() {
        return tokenHash;
    }
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getExpiration() {
        return expirationDate;
    }
    public void setExpiration(String expiration) {
        this.expirationDate = expiration;
    }
}
