package com.artistshowcase.api.service;

import com.artistshowcase.api.dto.VideoRequestDTO;
import com.artistshowcase.api.dto.VideoResponseDTO;
import com.artistshowcase.api.model.Video;
import com.artistshowcase.api.repository.VideoRepository;
import com.artistshowcase.api.util.YouTubeUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Page<VideoResponseDTO> findAll(int page, int size) {
        return videoRepository
                .findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toResponseDTO);
    }

    public Page<VideoResponseDTO> findFeatured(int page, int size) {
        return videoRepository
                .findByFeaturedTrueOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::toResponseDTO);
    }

    public VideoResponseDTO findById(Long id) {
        return videoRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new NoSuchElementException("Vídeo não encontrado: " + id));
    }

    public VideoResponseDTO create(VideoRequestDTO dto) {
        String videoId = YouTubeUtils.extractVideoId(dto.getYoutubeUrl());

        Video video = new Video();
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setYoutubeUrl(dto.getYoutubeUrl());
        video.setYoutubeVideoId(videoId);
        video.setFeatured(dto.isFeatured());

        return toResponseDTO(videoRepository.save(video));
    }

    public VideoResponseDTO update(Long id, VideoRequestDTO dto) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Vídeo não encontrado: " + id));

        String videoId = YouTubeUtils.extractVideoId(dto.getYoutubeUrl());

        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setYoutubeUrl(dto.getYoutubeUrl());
        video.setYoutubeVideoId(videoId);
        video.setFeatured(dto.isFeatured());

        return toResponseDTO(videoRepository.save(video));
    }

    public void delete(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new NoSuchElementException("Vídeo não encontrado: " + id);
        }
        videoRepository.deleteById(id);
    }

    private VideoResponseDTO toResponseDTO(Video video) {
        return new VideoResponseDTO(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getYoutubeUrl(),
                video.getYoutubeVideoId(),
                YouTubeUtils.buildThumbnailUrl(video.getYoutubeVideoId()),
                YouTubeUtils.buildEmbedUrl(video.getYoutubeVideoId()),
                video.isFeatured(),
                video.getCreatedAt()
        );
    }
}