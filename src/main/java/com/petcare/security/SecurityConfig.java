package com.petcare.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${firebase.verify-token:true}")
    private boolean verifyToken;

    // ── Publicly accessible endpoints ────────────────────────
    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/health",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/actuator/health",
            "/actuator/info",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                // Disable CSRF — stateless REST API
                .csrf(AbstractHttpConfigurer::disable)

                // CORS — ensure configured source is used
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Stateless sessions — no HttpSession
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Explicitly permit all preflights
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/vet/**").hasAnyRole("VET", "ADMIN")
                        .requestMatchers("/api/v1/shelter/**").hasAnyRole("SHELTER", "ADMIN")
                        .anyRequest().authenticated())

                // Insert Firebase filter BEFORE Spring's default auth filter
                .addFilterBefore(new FirebaseTokenFilter(verifyToken),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
