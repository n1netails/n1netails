package com.n1netails.n1netails.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class N1netailsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(N1netailsApiApplication.class, args);
	}
}
