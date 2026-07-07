package com.artistshowcase.api.security;

import com.artistshowcase.api.repository.AdminUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            AdminUserRepository adminUserRepository
    ) {
        this.jwtService = jwtService;
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Se não tem o header ou não começa com "Bearer ", passa adiante
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        if (jwtService.isTokenValid(token)) {
            String username = jwtService.extractUsername(token);

            // Verifica se o usuário ainda existe no banco
            adminUserRepository.findByUsername(username).ifPresent(user -> {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                        );
                // Registra a autenticação no contexto da requisição atual
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Usuário autenticado via JWT: {}", username);
            });
        }

        filterChain.doFilter(request, response);
    }
}