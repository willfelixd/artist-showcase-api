package com.artistshowcase.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongResponseDTO implements Serializable {
    private Long id;
    private String title;
    private String artist;
    private String genre;
    private String youtubeUrl;
    private boolean mostRequested;
}
