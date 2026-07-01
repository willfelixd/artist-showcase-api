package com.artistshowcase.api.repository;

import com.artistshowcase.api.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
