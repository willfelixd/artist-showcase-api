package com.artistshowcase.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Artista é obrigatório")
    private String artist;

    @NotBlank(message = "Gênero é obrigatório")
    private String genre;

    private String youtubeUrl;

    private boolean mostRequested;
}
