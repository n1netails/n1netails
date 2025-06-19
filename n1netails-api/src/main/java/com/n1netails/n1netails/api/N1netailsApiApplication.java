package com.n1netails.n1netails.api;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class N1netailsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(N1netailsApiApplication.class, args);
	}
}
