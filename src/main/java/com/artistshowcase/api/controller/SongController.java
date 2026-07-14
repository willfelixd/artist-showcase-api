package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.SongRequestDTO;
import com.artistshowcase.api.dto.SongResponseDTO;
import com.artistshowcase.api.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
@Tag(name = "Repertório", description = "Gerenciamento de músicas")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    @Operation(
            summary = "Listar músicas",
            description = "Retorna lista paginada com filtros opcionais de título e gênero — endpoint público"
    )
    public Page<SongResponseDTO> findAll(
            @Parameter(description = "Filtrar por título (busca parcial)")
            @RequestParam(required = false) String title,

            @Parameter(description = "Filtrar por gênero musical")
            @RequestParam(required = false) String genre,

            @Parameter(description = "Número da página (começa em 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Quantidade de itens por página")
            @RequestParam(defaultValue = "10") int size
    ) {
        return songService.findAll(title, genre, page, size);
    }

    @GetMapping("/most-requested")
    @Operation(
            summary = "Músicas mais pedidas",
            description = "Retorna lista paginada das músicas marcadas como mais pedidas — endpoint público"
    )
    public Page<SongResponseDTO> findMostRequested(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return songService.findMostRequested(page, size);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar música por ID",
            description = "Retorna uma música específica — endpoint público"
    )
    public SongResponseDTO findById(
            @Parameter(description = "ID da música")
            @PathVariable Long id
    ) {
        return songService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criar música",
            description = "Cria uma nova música no repertório — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public SongResponseDTO create(@Valid @RequestBody SongRequestDTO dto) {
        return songService.create(dto);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar música",
            description = "Atualiza os dados de uma música — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public SongResponseDTO update(
            @Parameter(description = "ID da música")
            @PathVariable Long id,
            @Valid @RequestBody SongRequestDTO dto
    ) {
        return songService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Deletar música",
            description = "Remove uma música do repertório — requer autenticação de admin"
    )
    @SecurityRequirement(name = "Bearer Auth")
    public void delete(
            @Parameter(description = "ID da música")
            @PathVariable Long id
    ) {
        songService.delete(id);
    }
}