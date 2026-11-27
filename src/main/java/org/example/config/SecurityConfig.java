
package org.example.config;

import org.example.util.JwtToken;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtToken jwtToken;

    public SecurityConfig(JwtToken jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Disable CSRF for this stateless REST API.
                .csrf(csrf -> csrf.disable())

                // Configure which endpoints are publicly accessible.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/**",
                                "/actuator/health"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // Do not create HTTP sessions.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Disable HTTP Basic authentication.
                .httpBasic(AbstractHttpConfigurer::disable)

                // Disable form-based login.
                .formLogin(AbstractHttpConfigurer::disable)

                // Register JWT filter before username/password authentication.
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtToken),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
