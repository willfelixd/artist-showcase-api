package com.artistshowcase.api.repository;

import com.artistshowcase.api.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, Long> {

    // Busca por título (parcial, sem case sensitive) E por gênero (opcional)
    @Query("""
        SELECT s FROM Song s
        WHERE (CAST(:title AS string) IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%')))
        AND (CAST(:genre AS string) IS NULL OR LOWER(s.genre) = LOWER(CAST(:genre AS string)))
        """)
    Page<Song> findByFilters(
            @Param("title") String title,
            @Param("genre") String genre,
            Pageable pageable
    );

    // Busca apenas as músicas marcadas como mais pedidas
    Page<Song> findByMostRequestedTrue(Pageable pageable);
}
