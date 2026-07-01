package com.artistshowcase.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO {
    private Long id;
    private String name;
    private String bio;
    private String profileImageUrl;
    private String instagramUrl;
    private String youtubeUrl;
}
