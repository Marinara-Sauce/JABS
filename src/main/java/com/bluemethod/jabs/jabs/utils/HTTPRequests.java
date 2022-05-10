package com.bluemethod.jabs.jabs.utils;

import com.bluemethod.jabs.jabs.requests.SteamIDRequest;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

import reactor.core.publisher.Flux;

public class HTTPRequests 
{
    public static Flux<String> getSteamProfile(String url, String key, String steamID)
    {
        WebClient client = WebClient.create();

        ((RequestBodySpec) client
            .get()
            .uri(url))
            .body(Flux.just(new SteamIDRequest(key, steamID)), SteamIDRequest.class)
            .retrieve()
            .bodyToFlux(String.class);

        return (Flux<String>) client;

        //TODO: Fix me
    }
}
