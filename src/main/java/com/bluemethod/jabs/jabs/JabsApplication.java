package com.bluemethod.jabs.jabs;

import com.bluemethod.jabs.jabs.utils.ServerAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JabsApplication {

	public static void main(String[] args) {
		ServerAuth.initToken();
		SpringApplication.run(JabsApplication.class, args);
	}

}
