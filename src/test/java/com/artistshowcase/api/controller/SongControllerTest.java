package com.artistshowcase.api.controller;

import com.artistshowcase.api.model.Song;
import com.artistshowcase.api.repository.SongRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();

        Song song1 = new Song();
        song1.setTitle("Garota de Ipanema");
        song1.setArtist("Tom Jobim");
        song1.setGenre("Bossa Nova");
        song1.setMostRequested(true);
        songRepository.save(song1);

        Song song2 = new Song();
        song2.setTitle("Aquarela do Brasil");
        song2.setArtist("Ary Barroso");
        song2.setGenre("Samba");
        song2.setMostRequested(false);
        songRepository.save(song2);
    }

    @Test
    @DisplayName("Deve listar músicas sem filtro")
    void findAll_noFilter() throws Exception {
        mockMvc.perform(get("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Deve filtrar músicas por título parcial")
    void findAll_filterByTitle() throws Exception {
        mockMvc.perform(get("/api/songs")
                        .param("title", "garota")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Garota de Ipanema"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Deve filtrar músicas por gênero")
    void findAll_filterByGenre() throws Exception {
        mockMvc.perform(get("/api/songs")
                        .param("genre", "Samba")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].genre").value("Samba"));
    }

    @Test
    @DisplayName("Deve retornar só as músicas mais pedidas")
    void findMostRequested() throws Exception {
        mockMvc.perform(get("/api/songs/most-requested")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].mostRequested").value(true));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar música inexistente")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/songs/99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 401 ao criar música sem token")
    void create_unauthorized() throws Exception {
        Map<String, Object> dto = Map.of(
                "title", "Nova Música",
                "artist", "Artista",
                "genre", "MPB",
                "mostRequested", false
        );

        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 400 ao criar música com título vazio")
    void create_blankTitle() throws Exception {
        Map<String, Object> dto = Map.of(
                "title", "",
                "artist", "Artista",
                "genre", "MPB",
                "mostRequested", false
        );

        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}