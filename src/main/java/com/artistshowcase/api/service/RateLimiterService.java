package com.artistshowcase.api.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    // Um "balde" de tokens por IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        // Limite: 3 mensagens por hora por IP
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillIntervally(3, Duration.ofHours(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public boolean isAllowed(String ip) {
        // Busca o balde do IP ou cria um novo se não existir
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());
        return bucket.tryConsume(1);
    }
}