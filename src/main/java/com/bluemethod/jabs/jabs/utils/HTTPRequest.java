package com.bluemethod.jabs.jabs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HTTPRequest {
    
    /***
     * Converts a map of parameters to a string suitable to be appended to a URL
     * 
     * @param params a map of the parameters
     * @return a string of the parameters to be appended to a URL
     * @throws UnsupportedEncodingException Error thrown if the encoding is unsuporrted (must be UTF-8)
     */
    private static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();

        if (params == null)
            return "";

        result.append("?");

        for (Map.Entry<String, String> entry : params.entrySet())
        {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        return result.toString().substring(0, result.toString().length() - 1);
    }

    /***
     * Sends a GET HTTP Request to a URL, returning the result
     * @param _url the url to send the request to
     * @param params a map of parameters to include (will be formatted)
     * @return the result
     * @throws IOException throws an exception if the URL is malformatted
     */
    public static Map<String, Object> getHTTPRequest(String _url, Map<String, String> params) throws IOException
    {
        //Setup our URL
        _url = _url + HTTPRequest.getParamsString(params);
        URL url = new URL(_url);

        //Configure our connection
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setInstanceFollowRedirects(false);
        
        int status = con.getResponseCode();

        // Read successful connections
        if (status < 299)
        {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream())
            );
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                content.append(inputLine);
            in.close();

            con.disconnect();
            @SuppressWarnings("unchecked")
            Map<String, Object> out = new ObjectMapper().readValue(content.toString(), Map.class);
            return out;
        }

        //Read errors
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getErrorStream())
        );

        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            content.append(inputLine);
        in.close();

        Map<String, Object> error = new HashMap<>();
        error.put("statuscode", status);
        error.put("content", content);
        con.disconnect();
        return error;
    }

}