package com.artistshowcase.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.artistshowcase.api.dto.LoginRequestDTO;
import com.artistshowcase.api.model.AdminUser;
import com.artistshowcase.api.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        adminUserRepository.deleteAll();
        AdminUser admin = new AdminUser();
        admin.setUsername("testadmin");
        admin.setPassword(passwordEncoder.encode("testpass123"));
        adminUserRepository.save(admin);
    }

    @Test
    @DisplayName("Deve retornar token JWT com credenciais válidas")
    void login_success() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("testadmin", "testpass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testadmin"));
    }

    @Test
    @DisplayName("Deve retornar 401 com senha incorreta")
    void login_wrongPassword() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("testadmin", "senhaerrada");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 com usuário inexistente")
    void login_unknownUser() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("naoexiste", "qualquercoisa");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 400 com campos em branco")
    void login_blankFields() throws Exception {
        LoginRequestDTO dto = new LoginRequestDTO("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}