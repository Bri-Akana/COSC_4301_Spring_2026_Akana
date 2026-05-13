package org.example.neonarkintaketracker.dto;

import java.time.Instant;
import java.util.List;

// Full creature snapshot with all observations — used for menu option 6
public record CreatureWithObservationsResponse(
        Long id,
        String name,
        String species,
        String dangerLevel,
        String condition,
        String status,
        String habitatName,
        Instant createdAt,
        List<ObservationResponse> observations
) {}
