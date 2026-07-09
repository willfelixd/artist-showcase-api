package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.SongRequestDTO;
import com.artistshowcase.api.dto.SongResponseDTO;
import com.artistshowcase.api.model.Song;
import com.artistshowcase.api.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song song;
    private SongRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        song = new Song(1L, "Garota de Ipanema", "Tom Jobim", "Bossa Nova", null, true);
        requestDTO = new SongRequestDTO("Garota de Ipanema", "Tom Jobim", "Bossa Nova", null, true);
    }

    @Test
    @DisplayName("Deve criar uma música com sucesso")
    void create_success() {
        when(songRepository.save(any(Song.class))).thenReturn(song);

        SongResponseDTO result = songService.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Garota de Ipanema");
        assertThat(result.getGenre()).isEqualTo("Bossa Nova");
        assertThat(result.isMostRequested()).isTrue();

        verify(songRepository, times(1)).save(any(Song.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar música inexistente")
    void findById_notFound() {
        when(songRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> songService.findById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve deletar uma música existente")
    void delete_success() {
        when(songRepository.existsById(1L)).thenReturn(true);
        doNothing().when(songRepository).deleteById(1L);

        assertThatNoException().isThrownBy(() -> songService.delete(1L));

        verify(songRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar música inexistente")
    void delete_notFound() {
        when(songRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> songService.delete(99L))
                .isInstanceOf(NoSuchElementException.class);

        verify(songRepository, never()).deleteById(any());
    }
}