package com.bluemethod.jabs.jabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JabsApplication {

	public static void main(String[] args) {
		//HTTPRequests.getSteamProfile("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/", "22240C044D4271252789763016CE9F45", "76561198169279086");
		SpringApplication.run(JabsApplication.class, args);
	}

}
