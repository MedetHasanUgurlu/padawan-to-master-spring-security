package com.medron.basicsecurity;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@SpringBootApplication
public class BasicSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicSecurityApplication.class, args);


    }

}
