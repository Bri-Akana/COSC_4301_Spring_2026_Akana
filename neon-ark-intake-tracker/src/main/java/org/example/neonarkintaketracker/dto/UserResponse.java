package org.example.neonarkintaketracker.dto;

// READ response for admin user listing
public record UserResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String role
) {}
