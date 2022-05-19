package com.bluemethod.jabs.jabs.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * A class dealing with generating secret tokens for the server,
 * access to this token provides the handler with access to server
 * functions, such as deleting and banning users
 * 
 * The token is saved at data/server_token.txt and should never be
 * shared with anyone
 */
public class ServerAuth 
{
    private static final String FILE_NAME = "data" + File.separator + "server_token.txt";

    private static String serverToken;

    /**
     * Initializes the token class
     * Creates a new token if data/server_token.txt does not exist
     * or reads from the file and sets tokenHash
     */
    public static void initToken()
    {
        try
        {
            File file = new File(FILE_NAME);
            file.getParentFile().mkdirs();

            //Generate a new token if the file is not found
            if (file.createNewFile())
                regenerateToken();

            //Otherwise, use the token hash in the file
            Scanner scanner = new Scanner(file);
            serverToken = scanner.nextLine();
            scanner.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Validates that an HTTP request came from a server owner by checking for the 
     * Token value in the request header
     * 
     * @param requestHeader the request header (sent from the controller)
     * @return true if the token is valid, false otherwise
     */
    public static boolean authToken(Map<String, String> requestHeader)
    {
        if (serverToken == null)
        {
            System.err.println("ServerAuth was not initialized!");
            return false;
        }

        //Make sure the request isn't malformed
        if (!requestHeader.containsKey("token"))
        {
            return false;
        }

        String token = requestHeader.get("token");

        return token.equals(serverToken);
    }

    /**
     * Regenerates the token, deleting the file and creating a new one
     */
    public static void regenerateToken()
    {
        File file = new File(FILE_NAME);

        if (file.exists())
        {
            file.delete();
        }

        // Gen the new token
        int leftLimit = 97; //a on ASCII
        int rightLimit = 122; //z on ASCII
        int tokenLen = 64;

        Random random = new Random();

        String token = random.ints(leftLimit, rightLimit + 1)
            .limit(tokenLen)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

        serverToken = token;

        FileWriter writer;

        try {
            writer = new FileWriter(file);
            writer.write(token);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
