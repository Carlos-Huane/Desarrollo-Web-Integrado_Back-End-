package com.grupo6.intranet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── Endpoints públicos ────────────────────────────
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias", "/api/categorias/**").permitAll()

                        // ── Usuarios ──────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // ── Categorías (escritura) ────────────────────────
                        .requestMatchers("/api/categorias/**").hasRole("ADMIN")

                        // ── Tickets ───────────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/tickets").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/tickets/**").hasAnyRole("ADMIN", "TECNICO", "CLIENTE")

                        // ── Artículos KB ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/articulos/**").authenticated()
                        .requestMatchers("/api/articulos/**").hasAnyRole("ADMIN", "TECNICO")

                        // ── Dashboard / Ranking / Export ──────────────────
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/ranking/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/export/**").hasAnyRole("ADMIN", "TECNICO")

                        // ── SLA (solo ADMIN) ──────────────────────────────
                        .requestMatchers("/api/sla/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
