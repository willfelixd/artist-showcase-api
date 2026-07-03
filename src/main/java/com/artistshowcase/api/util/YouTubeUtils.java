package com.artistshowcase.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTubeUtils {

    // Cobre os formatos mais comuns:
    // https://www.youtube.com/watch?v=ID
    // https://youtu.be/ID
    // https://www.youtube.com/embed/ID
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(
            "(?:youtube\\.com/(?:watch\\?v=|embed/)|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    );

    public static String extractVideoId(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL do YouTube não pode ser vazia");
        }
        Matcher matcher = YOUTUBE_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new IllegalArgumentException("URL do YouTube inválida: " + url);
        }
        return matcher.group(1);
    }

    public static String buildThumbnailUrl(String videoId) {
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    public static String buildEmbedUrl(String videoId) {
        return "https://www.youtube.com/embed/" + videoId;
    }

    private YouTubeUtils() {}
}