package com.bluemethod.jabs.jabs.controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.bluemethod.jabs.jabs.model.Ban;
import com.bluemethod.jabs.jabs.model.Token;
import com.bluemethod.jabs.jabs.model.User;
import com.bluemethod.jabs.jabs.persistence.TokenRepository;
import com.bluemethod.jabs.jabs.persistence.UserRepository;
import com.bluemethod.jabs.jabs.utils.Hashing;
import com.bluemethod.jabs.jabs.utils.ServerAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    @Value("${steam.webapikey}") private String apiKey;

    private Random random;

    /**
     * Initializes a UserController with a knwon userRepo and tokenRepo
     * Generally used for unit testing only
     * @param userRepo user repository to use
     * @param tokenRepo token repository to use
     * @throws NoSuchAlgorithmException
     */
    public UserController(UserRepository userRepo, TokenRepository tokenRepo) throws NoSuchAlgorithmException
    {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;

        //Because these are used in junits, this cannot be null
        this.canclientlistall = "true";

        random = SecureRandom.getInstanceStrong();
    }

    public UserController() throws NoSuchAlgorithmException { 
        random = SecureRandom.getInstanceStrong();
    }

    // --- API Keys --- //
    @Value("steam.webapikey") private String steamKey;

    /**
     * Fetches a list of all users
     * @return A list of every user and their information
     */
    @GetMapping("/user")
    public ResponseEntity<List<User>> listAll (@RequestHeader Map<String, String> header)
    {
        if (canclientlistall.equals("true") || ServerAuth.authToken(header))
            return new ResponseEntity<List<User>>(userRepo.findAll(), HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /**
     * Fetches a specific user using an ID
     * @param id The ID from the SQL table to use
     * @return The user object
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findUser (@PathVariable String id)
    {
        int userId = Integer.parseInt(id); 
        Optional<User> user = userRepo.findById(userId);

        if (!user.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        return new ResponseEntity<User>(user.get(), HttpStatus.OK);
    }

    /**
     * Finds a user from a username
     * 
     * @param body A string containing the username search query
     * @return A list of users with that username
     */
    @PostMapping("/user/search")
    public ResponseEntity<List<User>> searchUser(@RequestBody String username)
    {
        return new ResponseEntity<List<User>>(userRepo.findUsersByUsername(username), HttpStatus.OK);
    }

    /**
     * Finds a user based on the SteamID
     * 
     * @param body A string containing the ID
     * @return The user with that steamID, null if not found
     */
    @GetMapping("/user/steam/{id}")
    public ResponseEntity<User> searchUserBySteam(@PathVariable String id)
    {
        return new ResponseEntity<User>(userRepo.findUserBySteamId(id), HttpStatus.OK);
    }

    /**
     * Deletes a certain user by their ID
     * Only the server can run this command
     * 
     * @param id the user's ID to delete
     * @return true/false if deleted successfully
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable String id, @RequestHeader Map<String, String> header)
    {
        if (!ServerAuth.authToken(header))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        int userId = Integer.parseInt(id);
        Optional<User> user = userRepo.findById(userId);
        
        if (!user.isPresent())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        
        User u = user.get();
        userRepo.delete(u);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> body)
    {
        User u;

        //Login via steam id
        if (body.containsKey("steamID"))
        {
            String steamId = body.get("steamID");
            u = userRepo.findUserBySteamId(steamId);

            if (u == null)
                return new ResponseEntity<String>("FAILED_LOGIN_INVALID_STEAM_ID", HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<String>("FAILED_LOGIN_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED);
            }
            catch (NullPointerException e)
            {
                return new ResponseEntity<String>("FAILED_LOGIN_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED);
            }
            
            if (u == null)
                return new ResponseEntity<String>("FAILED_LOGIN_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED);

            if (u.getPassword().isEmpty())
                return new ResponseEntity<String>("FAILED_LOGIN_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED);

            if (!u.getPassword().equals(passwrdHashStr))
                return new ResponseEntity<String>("FAILED_LOGIN_BAD_CREDENTIALS", HttpStatus.UNAUTHORIZED);
        }
        
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        long token = random.nextLong();

        //Hash our token to send it to the DB
        byte[] tokenHash = Hashing.getSHA(Long.toString(token));
        String tokenHashString = Hashing.toHexString(tokenHash);

        Token t = new Token(tokenHashString, u.getId(), daysTillExpire, hoursTillExpire);
        tokenRepo.save(t);

        return new ResponseEntity<String>(Long.toString(token), HttpStatus.OK);
    }

    /**
     * Similar to login, creates a new user using one of two methods
     * User/Password, or SteamID
     * 
     * @param body either a username/password combo or a steam id
     * @return the new user
     */
    @PostMapping("/user/create")
    public ResponseEntity<User> createUser(@RequestBody Map<String, String> body)
    {
        User newUser;

        //Create based on steam ID
        if (body.containsKey("steamID"))
        {
            newUser = new User(body.get("steamID"), apiKey);
        }

        //Create a user based on username and password
        else if (body.containsKey("username") && body.containsKey("password"))
        {
            byte[] passwordHash = Hashing.getSHA(body.get("password"));
            String passwordHashStr = Hashing.toHexString(passwordHash);
            newUser = new User(body.get("username"), passwordHashStr, true);
        }

        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        userRepo.save(newUser);
        return new ResponseEntity<User>(newUser, HttpStatus.OK);
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
    public ResponseEntity<User> authenticateToken(@PathVariable String token)
    {
        return new ResponseEntity<User>(tokenRepo.authenticateToken(userRepo, token), HttpStatus.OK);
    }

    /**
     * Logs a user out based on their token, deleting the
     * token from the table.
     * 
     * @param token the token to log out
     * @return SUCCCESS if worked, NOT_FOUND if failed
     */
    @GetMapping("/user/logout/{token}")
    public ResponseEntity<User> logout(@PathVariable String token)
    {
        byte[] tokenHash = Hashing.getSHA(token);
        String tokenHashStr = Hashing.toHexString(tokenHash);

        Token t = tokenRepo.getTokenFromHash(tokenHashStr);

        if (t == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        
        tokenRepo.delete(t); 
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //User modification functions

    /**
     * Sets a user ban
     * @param body body of the request must be mapped as follows
     * id = the users id
     * perma = true if the user is perma banned
     * days = number of days to be banned
     * hours = number of hours to be banned
     * minutes = number of minutes to be banned
     * @param header the header of the request containing the server token
     * @return the http response, forbidden if there's a bad token, bad request if
     * malformed, not found if the user id doesn't exist, or ok if successful.
     */
    @PostMapping("/user/ban")
    public ResponseEntity<User> banUser(@RequestBody Map<String, String> body, @RequestHeader Map<String, String> header)
    {
        if (!ServerAuth.authToken(header)) return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        if (!body.containsKey("id")) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        int userId = Integer.parseInt(body.get("id"));
        Optional<User> u = userRepo.findById(userId);

        if (!u.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User user = u.get();

        if (body.containsKey("perma") && body.get("perma").equals("true"))
            user.setBan(new Ban(true));
        
        else
        {
            int daysBanned = 0;
            int hoursBanned = 0;
            int minutesBanned = 0;

            if (body.containsKey("days"))
                daysBanned = Integer.parseInt(body.get("days"));

            if (body.containsKey("hours"))
                hoursBanned = Integer.parseInt(body.get("hours"));

            if (body.containsKey("minutes"))
                minutesBanned = Integer.parseInt(body.get("minutes"));
            
            user.setBan(new Ban(daysBanned, hoursBanned, minutesBanned));
        }

        userRepo.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Checks a user's ban status
     * 
     * @param id the users id to check
     * @return if the user is banned
     */
    @GetMapping("/user/ban/{id}")
    public ResponseEntity<Boolean> getIsBanned(@PathVariable String id)
    {
        int userId = Integer.parseInt(id);
        Optional<User> u = userRepo.findById(userId);

        if (!u.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User user = u.get();

        return new ResponseEntity<Boolean>(user.playerBanned(), HttpStatus.OK);
    }
}
