package com.project.common_lib_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        exclude = {
                org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
                org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
        }
)
public class CommonLibServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonLibServiceApplication.class, args);
    }
}
