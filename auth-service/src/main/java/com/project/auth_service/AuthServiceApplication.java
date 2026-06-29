package com.project.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication()
@EntityScan(basePackages = {"com.project.auth_service.entity", "com.project.common_lib_service.entity"})
@EnableJpaRepositories(basePackages = {"com.project.auth_service.repository", "com.project.common_lib_service.repository"})
@EnableJpaAuditing(auditorAwareRef = "myAuditorProvider")
@ComponentScan(basePackages = {
        "com.project.auth_service.config",
        "com.project.common_lib_service.security",
        "com.project.common_lib_service.jwt",
        "com.project.common_lib_service.config",
        "com.project.common_lib_service.repository",
        "com.project.common_lib_service.service",
        "com.project.common_lib_service.exception",
        "com.project.common_lib_service.utils",
        "com.project.auth_service.cache",
        "com.project.auth_service.mapper",
        "com.project.auth_service.repository",
        "com.project.auth_service.service",
        "com.project.auth_service.service.facade",
        "com.project.auth_service.service.impl",
        "com.project.auth_service.controller",
        "com.project.auth_service.infrastructure",
})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}

