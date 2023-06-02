package com.medron.basicsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class BasicSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        /*
    }
        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/notice").authenticated()
                .requestMatchers("/test").permitAll();
        httpSecurity.formLogin();
        httpSecurity.httpBasic();
        return httpSecurity.build();

    */
        httpSecurity.authorizeHttpRequests(request -> {
            request
                    .requestMatchers("/notice").authenticated()
                    .requestMatchers("/test").permitAll();
        }).formLogin(Customizer.withDefaults()).httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }






}
