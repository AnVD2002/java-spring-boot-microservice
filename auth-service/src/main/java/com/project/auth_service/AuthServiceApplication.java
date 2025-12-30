package com.project.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@EntityScan(basePackages = {"com.project.auth_service.entity", "com.project.common_lib_service.entity"})
@ComponentScan(basePackages = {
        "com.project.auth_service.config",
        "com.project.common_lib_service.config",
        "com.project.common_lib_service.repository",
        "com.project.common_lib_service.service",
        "com.project.common_lib_service.exception",
        "com.project.common_lib_service.utils",
        "com.project.auth_service.repository",
        "com.project.auth_service.service",
        "com.project.auth_service.controller",

})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}

