package com.bluemethod.jabs.jabs.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Component
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String displayName;
    private String passwordHash;
    private String dateCreated;
    private String dateLastLoggedIn;

    public User(
            int id,
            String username,
            String displayName,
            String passwordHash,
            String dateCreated,
            String dateLastLoggedIn
    ) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.dateCreated = dateCreated;
        this.dateLastLoggedIn = dateLastLoggedIn;
    }

    public User() { }
}
