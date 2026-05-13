package org.example.neonarkcli.domain;


// Defines the allowed employment lifecycle states for a Neon Ark Warden.
// Kept separate from Role — they answer different questions.

public enum Status {
    ACTIVE,
    INACTIVE,
    RETIRED,
    DECEASED;

    /**
     * Case-insensitive lookup by name.
     * Returns null if the input does not match any valid status.
     * Used by WardenService to parse raw user input into a typed Status.
     */
    public static Status fromString(String input) {
        if (input == null || input.isBlank()) return null;
        for (Status s : values()) {
            if (s.name().equalsIgnoreCase(input.trim())) return s;
        }
        return null;
    }

    /** Returns a human-readable list of options for menu prompts. */
    public static String options() {
        return "ACTIVE | INACTIVE | RETIRED | DECEASED";
    }
}
