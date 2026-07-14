package com.artistshowcase.api.repository;

import com.artistshowcase.api.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {

    Page<Video> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Video> findByFeaturedTrueOrderByCreatedAtDesc(Pageable pageable);
}