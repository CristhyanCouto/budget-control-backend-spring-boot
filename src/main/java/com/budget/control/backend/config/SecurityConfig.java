package com.budget.control.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Security configuration for the application
    //Allowing for tests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/transaction-income/**").permitAll()
                                .requestMatchers("/transaction-expense/**").permitAll()
                                .requestMatchers("/transaction-benefit/**").permitAll()
                                .requestMatchers("/user/**").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/groups/**").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()); // Disable CSRF for testing purposes

        return http.build();
    }

    // Security password encoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}