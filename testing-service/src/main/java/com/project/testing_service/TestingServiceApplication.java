package com.project.testing_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.project.testing_service",
		"com.project.common_lib_service.config",
		"com.project.common_lib_service.security",
		"com.project.common_lib_service.exception",
		"com.project.common_lib_service.utils",
		"com.project.common_lib_service.kafka",
})
@EnableJpaAuditing(auditorAwareRef = "myAuditorProvider")
public class TestingServiceApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(TestingServiceApplication.class, args);
	}

}
