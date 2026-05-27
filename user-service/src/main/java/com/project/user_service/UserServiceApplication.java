package com.project.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.project.user_service",
		"com.project.common_lib_service.config",
		"com.project.common_lib_service.security",
		"com.project.common_lib_service.exception",
		"com.project.common_lib_service.utils",
})
@EnableJpaAuditing(auditorAwareRef = "myAuditorProvider")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
