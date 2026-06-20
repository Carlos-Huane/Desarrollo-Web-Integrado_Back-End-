package com.grupo6.intranet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ── Preflight CORS (OPTIONS) — siempre permitido ──
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

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
                        .requestMatchers("/api/tickets/tecnico/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers("/api/tickets/**").hasAnyRole("ADMIN", "TECNICO", "CLIENTE")

                        // ── Comentarios Ticket ────────────────────────────
                        .requestMatchers(HttpMethod.DELETE, "/api/comentarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/comentarios/**").hasAnyRole("ADMIN", "TECNICO", "CLIENTE")

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
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(401);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"status\":401,\"error\":\"No autenticado\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(403);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"status\":403,\"error\":\"Acceso denegado\"}");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://127.0.0.1:4200"
        ));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
