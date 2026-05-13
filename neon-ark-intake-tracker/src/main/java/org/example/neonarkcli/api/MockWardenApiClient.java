package org.example.neonarkcli.api;

import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;
import org.example.neonarkcli.model.Warden;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// Mock adapter. Loads seed CSV as a read-only database snapshot.
// Session-only mutations are held in memory and not persisted.
// Used when Main.java sets useMock = true.

public class MockWardenApiClient implements WardenGateway {

    private static final String CSV_PATH = "/wardens.csv";

    // Seed data loaded from CSV — READ ONLY, never modified
    private final List<Warden> seedWardens;

    // Session-only wardens added during runtime — NOT persisted
    private final List<Warden> sessionWardens = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────────────────────

    public MockWardenApiClient() {
        this.seedWardens = loadSeedData();
    }

    // ── WardenGateway implementation ──────────────────────────────────────────

    @Override
    public List<Warden> getAllWardens() {
        List<Warden> all = new ArrayList<>(seedWardens);
        all.addAll(sessionWardens);
        return all;
    }

    @Override
    public Warden createWarden(String name, Role role, Status status,
                                String hireDate, String badgeNumber) {
        int newId = 1000 + seedWardens.size() + sessionWardens.size() + 1;
        Warden w = new Warden(newId, name, role, status, hireDate, badgeNumber, false);
        sessionWardens.add(w);
        return w;
    }

    @Override
    public Warden updateWarden(int id, String field, String newValue) {
        // Simulated — session wardens could be updated here in a real mock
        return new Warden();
    }

    @Override
    public void softDeleteWarden(int id) {
        // Simulated — session wardens could be soft-deleted here in a real mock
    }

    // ── Uniqueness check (used by WardenService) ──────────────────────────────

    /**
     * Returns true if the given badge number already exists in seed or session data.
     * WardenService calls this before delegating a create to enforce uniqueness.
     */
    public boolean badgeExists(String badgeNumber) {
        for (Warden w : seedWardens) {
            if (w.getBadgeNumber().equalsIgnoreCase(badgeNumber)) return true;
        }
        for (Warden w : sessionWardens) {
            if (w.getBadgeNumber().equalsIgnoreCase(badgeNumber)) return true;
        }
        return false;
    }

    // ── Seed data loader ──────────────────────────────────────────────────────

    /**
     * Reads wardens.csv from the classpath.
     * This file is treated as READ-ONLY. It represents a frozen database snapshot.
     * The client may read this data. It may not write back to it.
     */
    private List<Warden> loadSeedData() {
        List<Warden> wardens = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream(CSV_PATH)) {
            if (is == null) {
                System.err.println("[ERROR] Seed file not found: " + CSV_PATH);
                return wardens;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.isBlank()) continue;

                String[] parts = line.split(",");
                if (parts.length < 7) continue;

                Role   role   = Role.fromString(parts[2].trim());
                Status status = Status.fromString(parts[3].trim());

                if (role == null || status == null) continue; // skip malformed rows

                wardens.add(new Warden(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        role,
                        status,
                        parts[4].trim(),
                        parts[5].trim(),
                        Boolean.parseBoolean(parts[6].trim())
                ));
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load seed data: " + e.getMessage());
        }

        return wardens;
    }
}
