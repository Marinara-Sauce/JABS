// package com.bluemethod.jabs.jabs.controller;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;

// import java.util.ArrayList;
// import java.util.Date;
// import java.util.LinkedHashMap;
// import java.util.Map;
// import java.util.Optional;
// import java.util.List;

// import com.bluemethod.jabs.jabs.model.Token;
// import com.bluemethod.jabs.jabs.model.User;
// import com.bluemethod.jabs.jabs.persistence.TokenRepository;
// import com.bluemethod.jabs.jabs.persistence.UserRepository;
// import com.bluemethod.jabs.jabs.utils.Hashing;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Test;

// @Tag("Controller-tier")
// public class UserControllerTest {

//     private final String TEST_USERNAME = "username";
//     private final String TEST_PASSWORD = "password";
//     private final String TEST_STEAM_ID = "1234";

//     private UserRepository mockUserRepo;
//     private TokenRepository mockTokenRepo;

//     private UserController cut;

//     @BeforeEach
//     void setup() {
//         mockUserRepo = mock(UserRepository.class);
//         mockTokenRepo = mock(TokenRepository.class);

//         cut = new UserController(mockUserRepo, mockTokenRepo);
//     }

//     @Test
//     void testAuthenticateToken() {
//         User user = new User();
//         when(mockTokenRepo.authenticateToken(any(), any())).thenReturn(user);
//         assertEquals(cut.authenticateToken("some token"), user);
//     }

//     @Test
//     void testCreateUser() {
//         //Test user creation via username/passwd
//         User testUser = new User("username", "password");
//         when(mockUserRepo.save(testUser)).thenReturn(testUser);
//         Map<String, String> body = new LinkedHashMap<>();
//         body.put("username", TEST_USERNAME);
//         body.put("password", TEST_PASSWORD);

//         User createdUser = cut.createUser(body);
//         assertTrue(createdUser.getUsername().equals(TEST_USERNAME));

//         //Fetch the hash for TEST_PASSWORD
//         String passwdHash = Hashing.toHexString(Hashing.getSHA(TEST_PASSWORD));
//         assertTrue(createdUser.getPassword().equals(passwdHash));

//         //Test user creation with steamID
//         testUser = new User(TEST_STEAM_ID);
//         body.clear();
//         body.put("steamID", TEST_STEAM_ID);

//         createdUser = cut.createUser(body);
//         assertTrue(createdUser.getSteamID().equals(TEST_STEAM_ID));

//         //Test a failed user creation from empty body
//         body.clear();
//         createdUser = cut.createUser(body);
//         assertNull(createdUser);
//     }

//     @Test
//     void testDeleteUser() {
//         User u = new User(TEST_USERNAME, TEST_PASSWORD);
//         when(mockUserRepo.findById(any())).thenReturn(Optional.of(u));
//         assertTrue(cut.deleteUser("0"));

//         //Check for failures
//         when(mockUserRepo.findById(any())).thenReturn(Optional.empty());
//         assertFalse(cut.deleteUser("0"));
//     }

//     @Test
//     void testFindUser() {
//         User u = new User(TEST_USERNAME, TEST_PASSWORD);
//         when(mockUserRepo.findById(any())).thenReturn(Optional.of(u));
//         assertNotNull(cut.findUser("0"));

//         //Check for failures
//         when(mockUserRepo.findById(any())).thenReturn(Optional.empty());
//         assertNull(cut.findUser("0"));
//     }

//     @Test
//     void testListAll() {
//         List<User> users = new ArrayList<>();
//         users.add(new User(TEST_USERNAME, TEST_PASSWORD));
//         users.add(new User("abc123", "inception"));

//         when(mockUserRepo.findAll()).thenReturn(users);
//         assertEquals(cut.listAll().size(), 2);
//     }

//     @Test
//     void testLoginUser() {

//         String passwordHash = Hashing.toHexString(Hashing.getSHA(TEST_PASSWORD));
//         User u = new User(TEST_USERNAME, passwordHash);
//         List<User> returnUsers = new ArrayList<>();
//         returnUsers.add(u);

//         when(mockUserRepo.findUsersByUsername(TEST_USERNAME)).thenReturn(returnUsers);

//         //First test Username/Password
//         Map<String, String> body = new LinkedHashMap<>();
//         body.put("username", TEST_USERNAME);
//         body.put("password", TEST_PASSWORD);

//         //Should go all the way
//         assertTrue(isNumeric(cut.loginUser(body)));

//         //Check for errors
//         when(mockUserRepo.findUsersByUsername(TEST_USERNAME)).thenReturn(null);
//         assertTrue(cut.loginUser(body).equals("FAILED_LOGIN_BAD_CREDENTIALS"));

//         //Test for empty password
//         body.put("password", "");
//         assertTrue(cut.loginUser(body).equals("FAILED_LOGIN_BAD_CREDENTIALS"));

//         //Test for incorrect password
//         body.put("password", "abc");
//         assertTrue(cut.loginUser(body).equals("FAILED_LOGIN_BAD_CREDENTIALS"));

//         //Test SteamID Logins
//         u = new User("123");
//         body.clear();
//         body.put("steamID", "123");

//         when(mockUserRepo.findUserBySteamId(any())).thenReturn(u);

//         //Should log in the user
//         assertTrue(isNumeric(cut.loginUser(body)));

//         //Check for null user
//         when(mockUserRepo.findUserBySteamId(any())).thenReturn(null);
//         assertTrue(cut.loginUser(body).equals("FAILED_LOGIN_INVALID_STEAM_ID"));

//         //Check for bad login alltogether
//         body.clear();
//         assertTrue(cut.loginUser(body).equals("FAILED_LOGIN_INVALID_LOGIN_PARAMETERS"));

//         //Test for throwing errors
//         body.put("password", "abc");
//         when(mockUserRepo.findUsersByUsername(any())).thenThrow(IndexOutOfBoundsException.class);
//         assertTrue(cut.loginUser(body).equals("FAILED_LOGIN_BAD_CREDENTIALS"));
//     }

//     @Test
//     void testLogout() {
//         String testToken = "123";
//         String testTokenHash = Hashing.toHexString(Hashing.getSHA(testToken));

//         Token t = new Token(testTokenHash, 1, new Date().toString());

//         when(mockTokenRepo.getTokenFromHash(eq(testTokenHash))).thenReturn(t);
//         assertTrue(cut.logout(testToken).equals("SUCCESS"));

//         when(mockTokenRepo.getTokenFromHash(eq(testTokenHash))).thenReturn(null);
//         assertTrue(cut.logout(testToken).equals("LOGOUT_FAILED_INVALID_TOKEN"));
//     }

//     @Test
//     void testSearchUser() {
//         List<User> userList = new ArrayList<>();
//         userList.add(new User());
//         userList.add(new User());

//         when(mockUserRepo.findUsersByUsername(any())).thenReturn(userList);
//         when(mockUserRepo.findUserBySteamId(any())).thenReturn(new User());

//         assertEquals(cut.searchUser("some username").size(), 2);
//         assertNotNull(cut.searchUserBySteam("1"));
//     }

//     @Test
//     void testUpdateUser() {

//         when(mockUserRepo.save(any())).thenReturn(new User());

//         assertNotNull(cut.updateUser("1", new User()));

//     }

//     public boolean isNumeric(String strnum)
//     {
//         if (strnum == null)
//             return false;
        
//         try {
//             @SuppressWarnings("unused")
//             Long l = Long.parseLong(strnum);
//             return true;
//         } catch (NumberFormatException e)
//         {
//             return false;
//         }
//     }
// }
