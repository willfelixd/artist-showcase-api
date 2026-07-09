package com.artistshowcase.api.config;

import com.artistshowcase.api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desativa CSRF — desnecessário em APIs stateless com JWT
                .csrf(csrf -> csrf.disable())

                // Stateless: o Spring não cria sessão HTTP
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Regras de acesso por rota
                .authorizeHttpRequests(auth -> auth

                        // Swagger UI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs"
                        ).permitAll()

                        // Público: login
                        .requestMatchers("/api/auth/**").permitAll()

                        // Público: leitura de conteúdo
                        .requestMatchers(HttpMethod.GET, "/api/artist/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/songs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/videos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/bookings/unavailable-dates").permitAll()

                        // Público: enviar contato e solicitar agendamento
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()

                        // Tudo mais exige ROLE_ADMIN
                        .anyRequest().hasRole("ADMIN")
                )

                // Adiciona o filtro JWT antes do filtro padrão de autenticação
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}