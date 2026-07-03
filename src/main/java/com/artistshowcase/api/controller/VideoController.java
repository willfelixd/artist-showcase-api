package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.VideoRequestDTO;
import com.artistshowcase.api.dto.VideoResponseDTO;
import com.artistshowcase.api.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public Page<VideoResponseDTO> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return videoService.findAll(page, size);
    }

    @GetMapping("/featured")
    public Page<VideoResponseDTO> findFeatured(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return videoService.findFeatured(page, size);
    }

    @GetMapping("/{id}")
    public VideoResponseDTO findById(@PathVariable Long id) {
        return videoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoResponseDTO create(@Valid @RequestBody VideoRequestDTO dto) {
        return videoService.create(dto);
    }

    @PutMapping("/{id}")
    public VideoResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody VideoRequestDTO dto
    ) {
        return videoService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        videoService.delete(id);
    }
}