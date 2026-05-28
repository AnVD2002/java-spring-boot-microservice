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
        "com.project.user_service.kafka",
        "com.project.user_service.dto",
        "com.project.user_service.entity",
        "com.project.user_service.repository",
        "com.project.user_service.service",
        "com.project.user_service.controller",

})
@EnableJpaAuditing(auditorAwareRef = "myAuditorProvider")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
