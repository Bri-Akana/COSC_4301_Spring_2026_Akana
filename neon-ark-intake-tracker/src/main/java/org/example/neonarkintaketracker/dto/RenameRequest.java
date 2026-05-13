package org.example.neonarkintaketracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Request body for PUT /api/creatures/{id}/name
public record RenameRequest(
        @NotBlank(message = "New name is required")
        @Size(max = 120, message = "Name cannot exceed 120 characters")
        String newName
) {}
