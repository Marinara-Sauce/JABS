package com.bluemethod.jabs.jabs.model;

import com.bluemethod.jabs.jabs.utils.Crypto;
import com.bluemethod.jabs.jabs.utils.DateConversions;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Entity
@Component
@Getter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String tokenHash;
    private int userId;
    private String expirationDate;

    /**
     * Generates a new token, generating the expiration date
     *
     * @param userId the user id this points to
     * @param daysToExpire the days till it expires
     * @param hoursToExpire the hours till it expires
     */
    public Token(String token, int userId, int daysToExpire, int hoursToExpire) {
        this.tokenHash = Crypto.sha256Hash(token);
        this.userId = userId;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, daysToExpire);
        cal.add(Calendar.HOUR_OF_DAY, hoursToExpire);

        this.expirationDate = DateConversions.SDF.format(cal.getTime());
    }

    public Token(int id, String tokenHash, int userId, String expirationDate) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expirationDate = expirationDate;
    }
    public Token() { }

    public boolean isExpired() {
        Date exp = DateConversions.convertStrToDate(expirationDate);
        Date today = new Date();
        return today.after(exp);
    }

    /**
     * Returns an un-hashed random long as a token
     * This is stored as an SHA-256 hash
     *
     * @return the token as a string
     */
    public static String generateToken() {
        //TODO: Use a secure method for RNG
        long token = new Random().nextLong();
        return Long.toString(token);
    }
}
