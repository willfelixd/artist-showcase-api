package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.ArtistDTO;
import com.artistshowcase.api.model.Artist;
import com.artistshowcase.api.repository.ArtistRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Cacheable(value = "artist", key = "'profile'")
    public ArtistDTO getProfile() {
        // Por enquanto, assumimos que existe sempre um único artista (id = 1)
        Artist artist = artistRepository.findById(1L)
                .orElseThrow(() -> new NoSuchElementException("Perfil da artista não encontrado"));

        return toDTO(artist);
    }

    @CacheEvict(value = "artist", allEntries = true)
    public ArtistDTO updateProfile(ArtistDTO dto) {
        Artist artist = artistRepository.findById(1L)
                .orElse(new Artist());

        artist.setName(dto.getName());
        artist.setBio(dto.getBio());
        artist.setProfileImageUrl(dto.getProfileImageUrl());
        artist.setInstagramUrl(dto.getInstagramUrl());
        artist.setYoutubeUrl(dto.getYoutubeUrl());

        Artist saved = artistRepository.save(artist);
        return toDTO(saved);
    }

    private ArtistDTO toDTO(Artist artist) {
        return new ArtistDTO(
                artist.getId(),
                artist.getName(),
                artist.getBio(),
                artist.getProfileImageUrl(),
                artist.getInstagramUrl(),
                artist.getYoutubeUrl()
        );
    }
}
