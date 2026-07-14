package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.ArtistDTO;
import com.artistshowcase.api.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist")
@Tag(name = "Perfil da Artista", description = "Gerenciamento do perfil da artista")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    @Operation(
            summary = "Buscar perfil",
            description = "Retorna o perfil completo da artista — endpoint público"
    )
    public ArtistDTO getProfile() {
        return artistService.getProfile();
    }

    @PutMapping
    @Operation(
            summary = "Atualizar perfil",
            description = "Atualiza o perfil da artista — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public ArtistDTO updateProfile(@RequestBody ArtistDTO dto) {
        return artistService.updateProfile(dto);
    }
}