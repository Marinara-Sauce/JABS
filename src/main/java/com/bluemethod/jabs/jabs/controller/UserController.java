package com.bluemethod.jabs.jabs.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserRepository repo;

    /**
     * Fetches a list of all users
     * @return A list of every user and their information
     */
    @GetMapping("/user")
    public List<User> listAll ()
    {
        return repo.findAll();
    }

    /**
     * Fetches a specific user using an ID
     * @param id The ID from the SQL table to use
     * @return The user object
     */
    @GetMapping("/user/{id}")
    public User findUser (@PathVariable String id)
    {
        int userId = Integer.parseInt(id);
        Optional<User> user = repo.findById(userId);

        if (!user.isPresent())
            return null;
        
        return user.get();
    }

    /**
     * Creates a new user and saves it to the DB
     * 
     * @param body The new user's information formatted as a map
     * @return The created user
     */
    @PostMapping("/user")
    public User createUser(@RequestBody Map<String, String> body)
    {
        String username = body.get("username");
        String steamID = body.get("steamID");

        return repo.save(new User(username, steamID));
    }

    /**
     * Updates a user by overwriting it in the DB
     * 
     * @param id The ID of the user to update (from SQL)
     * @param body The new user to overwrite the ID
     * @return The new User
     */
    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable String id, @RequestBody Map<String, String> body)
    {
        int userId = Integer.parseInt(id);

        Optional<User> user = repo.findById(userId);

        if (!user.isPresent())
            return null;
        
        User u = user.get();

        u.setUsername(body.get("username"));
        u.setSteamID(body.get("steamID"));

        return repo.save(u);
    }

    /**
     * Deletes a certain user by their ID
     * 
     * @param id the user's ID to delete
     * @return true/false if deleted successfully
     */
    @DeleteMapping("/user/{id}")
    public boolean deleteUser(@PathVariable String id)
    {
        int userId = Integer.parseInt(id);
        Optional<User> user = repo.findById(userId);
        
        if (!user.isPresent())
            return false;
        
        User u = user.get();
        repo.delete(u);
        return true;
    }
}
