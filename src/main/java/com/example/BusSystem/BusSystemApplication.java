package com.example.BusSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BusSystemApplication {

	public static void main(String[] args) {

		SpringApplication.run(BusSystemApplication.class, args);
	}

}
