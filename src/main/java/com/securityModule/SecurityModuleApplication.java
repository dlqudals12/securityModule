package com.securityModule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SecurityModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityModuleApplication.class, args);
    }

}
