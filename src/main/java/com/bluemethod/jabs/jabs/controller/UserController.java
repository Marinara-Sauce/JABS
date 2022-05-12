package com.bluemethod.jabs.jabs.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.TokenRepository;
import com.bluemethod.jabs.jabs.persistence.UserRepository;
import com.bluemethod.jabs.jabs.utils.Hashing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * User controller, communicates between the HTTP responses and
 * the SQL database
 */
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TokenRepository tokenRepo;

    // --- PERMISSIONS --- //
    @Value("${user.canclientlistall}") private String canclientlistall;

    /**
     * Fetches a list of all users
     * @return A list of every user and their information
     */
    @GetMapping("/user")
    public List<User> listAll ()
    {
        if (canclientlistall.equals("true"))
            return userRepo.findAll();

        return null;
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
        Optional<User> user = userRepo.findById(userId);

        if (!user.isPresent())
            return null;
        
        return user.get();
    }

    /**
     * Finds a user from a username
     * 
     * @param body A string containing the username search query
     * @return A list of users with that username
     */
    @PostMapping("/user/search")
    public List<User> searchUser(@RequestBody String username)
    {
        return userRepo.findUsersByUsername(username);
    }

    /**
     * Finds a user based on the SteamID
     * 
     * @param body A string containing the ID
     * @return The user with that steamID, null if not found
     */
    @GetMapping("/user/steam/{id}")
    public User searchUserBySteam(@PathVariable String id)
    {
        return userRepo.findUserBySteamId(id);
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
        String steamID = body.get("steamID");

        return userRepo.save(new User(steamID));
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

        Optional<User> user = userRepo.findById(userId);

        if (!user.isPresent())
            return null;
        
        User u = user.get();

        u.setUsername(body.get("username"));
        u.setSteamID(body.get("steamID"));

        return userRepo.save(u);
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
        Optional<User> user = userRepo.findById(userId);
        
        if (!user.isPresent())
            return false;
        
        User u = user.get();
        userRepo.delete(u);
        return true;
    }

    /**
     * TEMP SOLUTION: Login users based on the steam ID
     * 
     * @param steamId the users steam id passed through the body
     * @return the user's token
     */
    @PostMapping("/user/login/{steamId}")
    public String loginUser(@RequestBody Map<String, String> body)
    {
        String steamId = body.get("steamID");
        User u = userRepo.findUserBySteamId(steamId);

        if (u == null)
            //There'll be a more formal error eventually
            return "FAILED_LOGIN_INVALID_STEAM_ID";
        
        //Generate a random token
        //TODO: More secure method for RNG
        long token = new Random().nextLong();

        //Hash our token to send it to the DB
        byte[] tokenHash = Hashing.getSHA(Long.toString(token));
        String tokenHashString = Hashing.toHexString(tokenHash);

        Token t = new Token(tokenHashString, u.getId());
        tokenRepo.save(t);

        return Long.toString(token);
    }
    
    /**
     * Authenticates a user ticket, this is
     * what's used by a user to authorize themselves
     * 
     * The tokens are stored in the DB table tokens as a
     * SHA-256 hash
     * 
     * @param ticket the ticket
     * @return the user, null if not found
     */
    @GetMapping("/user/auth/{token}")
    public User authenticateToken(@PathVariable String token)
    {
        //Converts the token to a SHA-256 hash, uses this to look it
        //up in the DB
        byte[] tokenHash = Hashing.getSHA(token);
        String tokenHashString = Hashing.toHexString(tokenHash);

        int userId = tokenRepo.getUserIdFromToken(tokenHashString);

        if (userId == -1)
            return null;

        Optional<User> user = userRepo.findById(userId);

        if (!user.isPresent())
            return null;
        
        return user.get();
    }
}
