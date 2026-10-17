
package com.lostlink.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpStatus;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(org.springframework.security.config.Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authenticationProvider(authenticationProvider)
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login", "/error").permitAll()
                .requestMatchers("/api/claims/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/items/mine").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/items", "/api/items/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/items", "/api/items/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/items/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/items/**").authenticated()
                .anyRequest().denyAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
