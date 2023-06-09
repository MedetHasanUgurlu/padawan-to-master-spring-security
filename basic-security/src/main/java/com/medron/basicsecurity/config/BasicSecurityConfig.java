package com.medron.basicsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class BasicSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/test").authenticated()
                .requestMatchers("/notice").permitAll().and().csrf().disable();
        httpSecurity.formLogin();
        httpSecurity.httpBasic();
        return httpSecurity.build();


//        httpSecurity.authorizeHttpRequests(request -> {
//
//            request
//                    .requestMatchers("/notice").authenticated()
//                    .requestMatchers("/test").permitAll();
//        }).formLogin(Customizer.withDefaults()).httpBasic(Customizer.withDefaults());
//        return httpSecurity.build();
    }


//    @Bean
//    UserDetailsService userDetailsService(DataSource dataSource){
//        return new JdbcUserDetailsManager(dataSource);
//    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
