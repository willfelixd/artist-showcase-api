package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.SongRequestDTO;
import com.artistshowcase.api.dto.SongResponseDTO;
import com.artistshowcase.api.model.Song;
import com.artistshowcase.api.repository.SongRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    // Cacheia o resultado — na próxima chamada com os mesmos parâmetros,
    // retorna do Redis sem tocar no banco
    @Cacheable(value = "songs", key = "#title + '-' + #genre + '-' + #page + '-' + #size")
    public Page<SongResponseDTO> findAll(String title, String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return songRepository.findByFilters(title, genre, pageable)
                .map(this::toResponseDTO);
    }

    @Cacheable(value = "songs", key = "'most-requested-' + #page + '-' + #size")
    public Page<SongResponseDTO> findMostRequested(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        return songRepository.findByMostRequestedTrue(pageable)
                .map(this::toResponseDTO);
    }

    public SongResponseDTO findById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Música não encontrada: " + id));
        return toResponseDTO(song);
    }

    // Ao criar, atualizar ou deletar, invalida TODO o cache de songs
    @CacheEvict(value = "songs", allEntries = true)
    public SongResponseDTO create(SongRequestDTO dto) {
        Song song = toEntity(dto);
        return toResponseDTO(songRepository.save(song));
    }

    @CacheEvict(value = "songs", allEntries = true)
    public SongResponseDTO update(Long id, SongRequestDTO dto) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Música não encontrada: " + id));
        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setGenre(dto.getGenre());
        song.setYoutubeUrl(dto.getYoutubeUrl());
        song.setMostRequested(dto.isMostRequested());
        return toResponseDTO(songRepository.save(song));
    }

    @CacheEvict(value = "songs", allEntries = true)
    public void delete(Long id) {
        if (!songRepository.existsById(id)) {
            throw new NoSuchElementException("Música não encontrada: " + id);
        }
        songRepository.deleteById(id);
    }

    // --- Conversões privadas ---

    private SongResponseDTO toResponseDTO(Song song) {
        return new SongResponseDTO(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getGenre(),
                song.getYoutubeUrl(),
                song.isMostRequested()
        );
    }

    private Song toEntity(SongRequestDTO dto) {
        Song song = new Song();
        song.setTitle(dto.getTitle());
        song.setArtist(dto.getArtist());
        song.setGenre(dto.getGenre());
        song.setYoutubeUrl(dto.getYoutubeUrl());
        song.setMostRequested(dto.isMostRequested());
        return song;
    }
}
