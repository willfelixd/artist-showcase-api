package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.VideoRequestDTO;
import com.artistshowcase.api.dto.VideoResponseDTO;
import com.artistshowcase.api.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "Vídeos", description = "Gerenciamento de vídeos do YouTube")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    @Operation(
            summary = "Listar vídeos",
            description = "Retorna lista paginada de vídeos ordenados por data — endpoint público"
    )
    public Page<VideoResponseDTO> findAll(
            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "12") int size
    ) {
        return videoService.findAll(page, size);
    }

    @GetMapping("/featured")
    @Operation(
            summary = "Vídeos em destaque",
            description = "Retorna lista paginada de vídeos marcados como destaque — endpoint público"
    )
    public Page<VideoResponseDTO> findFeatured(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return videoService.findFeatured(page, size);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar vídeo por ID",
            description = "Retorna um vídeo específico com thumbnailUrl e embedUrl — endpoint público"
    )
    public VideoResponseDTO findById(
            @Parameter(description = "ID do vídeo")
            @PathVariable Long id
    ) {
        return videoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Adicionar vídeo",
            description = "Cadastra um novo vídeo do YouTube — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public VideoResponseDTO create(@Valid @RequestBody VideoRequestDTO dto) {
        return videoService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar vídeo",
            description = "Atualiza os dados de um vídeo — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public VideoResponseDTO update(
            @Parameter(description = "ID do vídeo")
            @PathVariable Long id,
            @Valid @RequestBody VideoRequestDTO dto
    ) {
        return videoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar vídeo",
            description = "Remove um vídeo — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public void delete(
            @Parameter(description = "ID do vídeo")
            @PathVariable Long id
    ) {
        videoService.delete(id);
    }
}