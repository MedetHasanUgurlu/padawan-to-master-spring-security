package com.medron.basicsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;

@SpringBootApplication
@EnableWebSecurity
public class BasicSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicSecurityApplication.class, args);


    }

}
