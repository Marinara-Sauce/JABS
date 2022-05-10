package com.bluemethod.jabs.jabs.requests;

public class SteamIDRequest 
{
    private String key;
    private String id;

    public SteamIDRequest(String key, String id)
    {
        this.key = key;
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public String getId()
    {
        return id;
    }
}
