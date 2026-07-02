package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.SongRequestDTO;
import com.artistshowcase.api.dto.SongResponseDTO;
import com.artistshowcase.api.service.SongService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // GET /api/songs?title=amor&genre=mpb&page=0&size=10
    @GetMapping
    public Page<SongResponseDTO> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return songService.findAll(title, genre, page, size);
    }

    // GET /api/songs/most-requested
    @GetMapping("/most-requested")
    public Page<SongResponseDTO> findMostRequested(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return songService.findMostRequested(page, size);
    }

    // GET /api/songs/1
    @GetMapping("/{id}")
    public SongResponseDTO findById(@PathVariable Long id) {
        return songService.findById(id);
    }

    // POST /api/songs
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SongResponseDTO create(@Valid @RequestBody SongRequestDTO dto) {
        return songService.create(dto);
    }

    // PUT /api/songs/1
    @PutMapping("/{id}")
    public SongResponseDTO update(@PathVariable Long id, @Valid @RequestBody SongRequestDTO dto) {
        return songService.update(id, dto);
    }

    // DELETE /api/songs/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        songService.delete(id);
    }
}
