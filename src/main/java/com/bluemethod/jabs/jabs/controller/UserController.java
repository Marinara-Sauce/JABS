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
    
    // --- CONFIG SETTINGS --- //
    @Value("${user.tokenDaysTillExpire}") private int daysTillExpire;
    @Value("${user.tokenHoursTillExpire}") private int hoursTillExpire;

    /**
     * Initializes a UserController with a knwon userRepo and tokenRepo
     * Generally used for unit testing only
     * @param userRepo user repository to use
     * @param tokenRepo token repository to use
     */
    public UserController(UserRepository userRepo, TokenRepository tokenRepo)
    {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;

        //Because these are used in junits, this cannot be null
        this.canclientlistall = "true";
    }

    public UserController() { }

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
     * Updates a user by overwriting it in the DB
     * 
     * @param id The ID of the user to update (from SQL)
     * @param user the new user to overwrite at that id
     * @return The new User
     */
    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User user)
    {
        int userId = Integer.parseInt(id);

        user.setId(userId);

        return userRepo.save(user);
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
     * Offers methods for the user to log in. Currently supported:
     * Common user/password where user = username and password = password
     * Steam where steamID = The user's 64bit token (Soon to be depracated)
     * 
     * @param body the requests body with one of the following tags
     * @return the user's token
     */
    @PostMapping("/user/login")
    public String loginUser(@RequestBody Map<String, String> body)
    {
        User u;

        //Login via steam id
        if (body.containsKey("steamID"))
        {
            String steamId = body.get("steamID");
            u = userRepo.findUserBySteamId(steamId);

            if (u == null)
                return "FAILED_LOGIN_INVALID_STEAM_ID";
        }

        //Login with username and password
        else if (body.containsKey("password"))
        {
            //Convert the password to a Hash
            String username = body.get("username");
            byte[] passwdHash = Hashing.getSHA(body.get("password"));
            String passwrdHashStr = Hashing.toHexString(passwdHash);

            //Locate the user
            try
            {
                u = userRepo.findUsersByUsername(username).get(0);
            }
            catch (IndexOutOfBoundsException e)
            {
                return "FAILED_LOGIN_BAD_CREDENTIALS";
            }
            catch (NullPointerException e)
            {
                return "FAILED_LOGIN_BAD_CREDENTIALS";
            }
            
            if (u == null)
                return "FAILED_LOGIN_BAD_CREDENTIALS";

            if (u.getPassword().isEmpty())
                return "FAILED_LOGIN_BAD_CREDENTIALS";

            if (!u.getPassword().equals(passwrdHashStr))
                return "FAILED_LOGIN_BAD_CREDENTIALS";
        }
        
        else
        {
            return "FAILED_LOGIN_INVALID_LOGIN_PARAMETERS";
        }

        //Generate a random token
        //TODO: More secure method for RNG
        long token = new Random().nextLong();

        //Hash our token to send it to the DB
        byte[] tokenHash = Hashing.getSHA(Long.toString(token));
        String tokenHashString = Hashing.toHexString(tokenHash);

        Token t = new Token(tokenHashString, u.getId(), daysTillExpire, hoursTillExpire);
        tokenRepo.save(t);

        return Long.toString(token);
    }

    /**
     * Similar to login, creates a new user using one of two methods
     * User/Password, or SteamID
     * 
     * @param body either a username/password combo or a steam id
     * @return the new user
     */
    @PostMapping("/user/create")
    public User createUser(@RequestBody Map<String, String> body)
    {
        User newUser;

        //Create based on steam ID
        if (body.containsKey("steamID"))
        {
            newUser = new User(body.get("steamID"));
        }

        //Create a user based on username and password
        else if (body.containsKey("username") && body.containsKey("password"))
        {
            byte[] passwordHash = Hashing.getSHA(body.get("password"));
            String passwordHashStr = Hashing.toHexString(passwordHash);
            newUser = new User(body.get("username"), passwordHashStr);
        }

        else
            return null;

        userRepo.save(newUser);
        return newUser;
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
        return tokenRepo.authenticateToken(userRepo, token);
    }

    /**
     * Logs a user out based on their token, deleting the
     * token from the table.
     * 
     * @param token the token to log out
     * @return SUCCCESS if worked, NOT_FOUND if failed
     */
    @GetMapping("/user/logout/{token}")
    public String logout(@PathVariable String token)
    {
        byte[] tokenHash = Hashing.getSHA(token);
        String tokenHashStr = Hashing.toHexString(tokenHash);

        Token t = tokenRepo.getTokenFromHash(tokenHashStr);

        if (t == null)
            return "LOGOUT_FAILED_INVALID_TOKEN";
        
        tokenRepo.delete(t);
        return "SUCCESS";
    }
}
