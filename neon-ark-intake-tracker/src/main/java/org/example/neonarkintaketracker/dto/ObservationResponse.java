package org.example.neonarkintaketracker.dto;

import java.time.Instant;

// One observation record — includes author name and timestamp
public record ObservationResponse(
        Long id,
        String authorName,
        String notes,
        Instant observedAt
) {}
