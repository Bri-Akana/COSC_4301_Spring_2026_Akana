package org.example.neonarkintaketracker.dto;

// Response for PUT /api/creatures/{id}/name — shows old and new name clearly
public record RenameResponse(
        Long id,
        String oldName,
        String newName,
        String habitatName
) {}
