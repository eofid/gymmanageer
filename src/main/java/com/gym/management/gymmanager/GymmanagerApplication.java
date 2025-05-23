package com.gym.management.gymmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class GymmanagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(GymmanagerApplication.class, args);
	}
}
