package com.example.eumprotected.simplejobrestclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SimpleJobRestClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleJobRestClientApplication.class, args);
	}

}
