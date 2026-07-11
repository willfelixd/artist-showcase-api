package com.artistshowcase.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO implements Serializable {
    private Long id;
    private String name;
    private String bio;
    private String profileImageUrl;
    private String instagramUrl;
    private String youtubeUrl;
}
