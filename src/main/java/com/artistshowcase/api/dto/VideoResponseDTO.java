package com.artistshowcase.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String youtubeUrl;
    private String youtubeVideoId;
    private String thumbnailUrl;
    private String embedUrl;
    private boolean featured;
    private LocalDateTime createdAt;
}