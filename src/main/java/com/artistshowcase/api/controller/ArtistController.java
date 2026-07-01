package com.artistshowcase.api.controller;

import com.artistshowcase.api.dto.ArtistDTO;
import com.artistshowcase.api.service.ArtistService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ArtistDTO getProfile() {
        return artistService.getProfile();
    }

    @PutMapping
    public ArtistDTO updateProfile(@RequestBody ArtistDTO dto) {
        return artistService.updateProfile(dto);
    }
}
