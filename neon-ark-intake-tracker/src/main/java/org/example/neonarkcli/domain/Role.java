package org.example.neonarkcli.domain;

// Defines the allowed job roles for a Neon Ark Warden.
// Using an enum means invalid values cannot enter the system.

public enum Role {
    XENOBIOLOGIST,
    CARETAKER,
    SECURITY,
    FIELD;

    /**
     * Case-insensitive lookup by name.
     * Returns null if the input does not match any valid role.
     * Used by WardenService to parse raw user input into a typed Role.
     */
    public static Role fromString(String input) {
        if (input == null || input.isBlank()) return null;
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(input.trim())) return r;
        }
        return null;
    }

    /** Returns a human-readable list of options for menu prompts. */
    public static String options() {
        return "XENOBIOLOGIST | CARETAKER | SECURITY | FIELD";
    }
}
