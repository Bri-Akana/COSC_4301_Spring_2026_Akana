package org.example.neonarkcli.menu;

import org.example.neonarkcli.api.HttpCreatureApiClient;
import org.example.neonarkcli.api.HttpCreatureApiClient.*;
import org.example.neonarkcli.model.Creature;
import org.example.neonarkcli.model.Observation;
import org.example.neonarkcli.model.SystemUser;

import java.util.List;
import java.util.Scanner;

/**
 * CreatureMenu is the user interaction layer for the Neon Ark CLI.
 * Each numbered option maps to exactly one HTTP request per the contract.
 * No business rules live here — all data flows through HttpCreatureApiClient.
 */
public class CreatureMenu {

    private final HttpCreatureApiClient api;
    private final Scanner scanner;

    public CreatureMenu(HttpCreatureApiClient api, Scanner scanner) {
        this.api     = api;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> handleListAll();
                case "2" -> handleGetById();
                case "3" -> handleCreate();
                case "4" -> handleRename();
                case "5" -> handleSoftDelete();
                case "6" -> handleObservations();
                case "7" -> handleFeedingTime();
                case "8" -> handleAdminUsers();
                case "0" -> {
                    System.out.print("  Are you sure you want to exit? (y/n): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                        System.out.println();
                        System.out.println("  Logging out of Neon Ark CLI. Goodbye.");
                        running = false;
                        return;
                    }
                }
                default -> System.out.println("  Invalid selection. Please enter 0 through 8.");
            }

            if (running) {
                System.out.println();
                System.out.println("  Press ENTER to return to the menu...");
                scanner.nextLine();
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("  =====================================");
        System.out.println("         NEON ARK CLI SYSTEM           ");
        System.out.println("  =====================================");
        System.out.println();
        System.out.println("  1. List all creatures");
        System.out.println("  2. View creature by ID");
        System.out.println("  3. Register new creature");
        System.out.println("  4. Rename creature");
        System.out.println("  5. Remove creature");
        System.out.println("  6. View creature observations/notes");
        System.out.println("  7. Find creatures by feeding time");
        System.out.println();
        System.out.println("  --- Admin Only ---");
        System.out.println("  8. View all system users");
        System.out.println();
        System.out.println("  0. Exit");
        System.out.println("  -------------------------------------");
        System.out.print("  Select an option: ");
    }

    // ── 1. List all creatures ─────────────────────────────────────────────────
    private void handleListAll() {
        System.out.println("  Fetching all creatures...");
        ApiResult<List<Creature>> result = api.listAll();

        if (!result.success) {
            printError(result);
            return;
        }

        List<Creature> creatures = result.data;
        if (creatures.isEmpty()) {
            System.out.println("  No creatures found in the system.");
            return;
        }

        printCreatureTable(creatures);
        System.out.printf("%n  Total: %d creature(s)%n", creatures.size());
    }

    // ── 2. View creature by ID ────────────────────────────────────────────────
    private void handleGetById() {
        System.out.print("  Enter creature ID: ");
        long id = readLong();
        if (id <= 0) return;

        ApiResult<Creature> result = api.getById(id);

        if (!result.success) {
            printError(result);
            return;
        }

        printCreatureTable(List.of(result.data));
    }

    // ── 3. Register new creature ──────────────────────────────────────────────
    private void handleCreate() {
        System.out.println("  -- Register New Creature ---------------------------");
        System.out.println();

        System.out.print("  Name             : ");
        String name = scanner.nextLine().trim();

        System.out.print("  Species          : ");
        String species = scanner.nextLine().trim();

        System.out.println("  Danger Level options: LOW | MEDIUM | HIGH | EXTREME");
        System.out.print("  Danger Level     : ");
        String dangerLevel = scanner.nextLine().trim().toUpperCase();

        System.out.println("  Condition options: STABLE | RECOVERING | AGGRESSIVE | QUARANTINED");
        System.out.print("  Condition        : ");
        String condition = scanner.nextLine().trim().toUpperCase();

        System.out.print("  Habitat ID       : ");
        long habitatId = readLong();
        if (habitatId <= 0) return;

        ApiResult<Creature> result = api.create(name, species, dangerLevel, condition, habitatId);

        if (!result.success) {
            printError(result);
            return;
        }

        System.out.println();
        System.out.println("  Creature registered successfully!");
        printCreatureTable(List.of(result.data));
    }

    // ── 4. Rename creature ────────────────────────────────────────────────────
    private void handleRename() {
        System.out.print("  Enter creature ID to rename: ");
        long id = readLong();
        if (id <= 0) return;

        System.out.print("  Enter new name: ");
        String newName = scanner.nextLine().trim();

        System.out.printf("  Confirm renaming creature #%d to \"%s\"? (y/n): ", id, newName);
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("  Rename cancelled.");
            return;
        }

        ApiResult<String> result = api.rename(id, newName);

        if (!result.success) {
            printError(result);
            return;
        }

        System.out.println();
        System.out.println("  " + result.data);
    }

    // ── 5. Remove creature (soft delete) ──────────────────────────────────────
    private void handleSoftDelete() {
        System.out.print("  Enter creature ID to remove: ");
        long id = readLong();
        if (id <= 0) return;

        System.out.printf("  Confirm removing creature #%d? This cannot be undone. (y/n): ", id);
        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println("  Remove cancelled.");
            return;
        }

        ApiResult<String> result = api.softDelete(id);

        if (!result.success) {
            printError(result);
            return;
        }

        System.out.println();
        System.out.println("  " + result.data);
    }

    // ── 6. View creature observations ─────────────────────────────────────────
    private void handleObservations() {
        System.out.print("  Enter creature ID: ");
        long id = readLong();
        if (id <= 0) return;

        ApiResult<CreatureWithObservations> result = api.getWithObservations(id);

        if (!result.success) {
            printError(result);
            return;
        }

        CreatureWithObservations data = result.data;

        System.out.println();
        System.out.println("  -- Creature Details " + "-".repeat(80));
        printCreatureTable(List.of(data.creature));

        System.out.println();
        System.out.println("  -- Observations " + "-".repeat(84));

        if (data.observations.isEmpty()) {
            System.out.println("  No observations recorded for this creature.");
            return;
        }

        String fmt     = "  %-6s  %-20s  %-22s  %-45s%n";
        String divider = "  " + "-".repeat(100);

        System.out.printf(fmt, "ID", "AUTHOR", "OBSERVED AT", "NOTES (truncated)");
        System.out.println(divider);

        for (Observation obs : data.observations) {
            String shortNotes = obs.getNotes().length() > 45
                    ? obs.getNotes().substring(0, 42) + "..."
                    : obs.getNotes();
            String time = obs.getObservedAt().length() > 19
                    ? obs.getObservedAt().substring(0, 19)
                    : obs.getObservedAt();
            System.out.printf(fmt, obs.getId(), obs.getAuthorName(), time, shortNotes);
        }

        System.out.println(divider);
        System.out.printf("  Total observations: %d%n", data.observations.size());

        // Print full notes below the table
        System.out.println();
        System.out.println("  -- Full Observation Notes " + "-".repeat(74));
        for (Observation obs : data.observations) {
            String time = obs.getObservedAt().length() > 19
                    ? obs.getObservedAt().substring(0, 19)
                    : obs.getObservedAt();
            System.out.println();
            System.out.printf("  [#%d] %s — %s%n", obs.getId(), obs.getAuthorName(), time);
            System.out.println("  " + obs.getNotes());
        }
        System.out.println();
    }

    // ── 7. Find creatures by feeding time ─────────────────────────────────────
    private void handleFeedingTime() {
        System.out.println("  Enter feeding time in HH:MM format (example: 08:00)");
        System.out.print("  Time: ");
        String time = scanner.nextLine().trim();

        ApiResult<FeedingResult> result = api.getByFeedingTime(time);

        if (!result.success) {
            printError(result);
            return;
        }

        System.out.println();
        System.out.println("  " + result.data.message);

        if (!result.data.creatures.isEmpty()) {
            System.out.println();
            printCreatureTable(result.data.creatures);
        }
    }

    // ── 8. Admin: view all users ──────────────────────────────────────────────
    private void handleAdminUsers() {
        System.out.println("  Fetching all system users...");
        ApiResult<List<SystemUser>> result = api.getAllUsers();

        if (!result.success) {
            printError(result);
            return;
        }

        List<SystemUser> users = result.data;
        if (users.isEmpty()) {
            System.out.println("  No users found.");
            return;
        }

        String fmt     = "  %-6s  %-22s  %-30s  %-15s  %-12s%n";
        String divider = "  " + "-".repeat(95);

        System.out.println();
        System.out.printf(fmt, "ID", "FULL NAME", "EMAIL", "PHONE", "ROLE");
        System.out.println(divider);

        for (SystemUser u : users) {
            System.out.printf(fmt,
                    u.getId(),
                    u.getFullName(),
                    u.getEmail(),
                    u.getPhone() == null || u.getPhone().isBlank() ? "—" : u.getPhone(),
                    u.getRole());
        }

        System.out.println(divider);
        System.out.printf("  Total users: %d%n", users.size());
    }

    // ── Shared display helpers ────────────────────────────────────────────────

    private void printCreatureTable(List<Creature> creatures) {
        String fmt     = "  %-6s  %-18s  %-18s  %-8s  %-14s  %-8s  %s%n";
        // 2 + 6 + 2 + 18 + 2 + 18 + 2 + 8 + 2 + 14 + 2 + 8 + 2 + ~30 (habitat) = ~100
        String divider = "  " + "-".repeat(100);

        System.out.println();
        System.out.printf(fmt, "ID", "NAME", "SPECIES", "DANGER", "CONDITION", "STATUS", "HABITAT");
        System.out.println(divider);

        for (Creature c : creatures) {
            System.out.printf(fmt,
                    c.getId(),
                    c.getName(),
                    c.getSpecies(),
                    c.getDangerLevel(),
                    c.getCondition(),
                    c.getStatus(),
                    c.getHabitatName());
        }

        System.out.println(divider);
    }

    private void printError(ApiResult<?> result) {
        System.out.println();
        System.out.println("  Error [HTTP " + result.statusCode + "]: " + result.error);
    }

    private long readLong() {
        try {
            String input = scanner.nextLine().trim();
            long val = Long.parseLong(input);
            if (val <= 0) {
                System.out.println("  ID must be a positive number.");
                return -1;
            }
            return val;
        } catch (NumberFormatException e) {
            System.out.println("  Invalid ID — must be a number.");
            return -1;
        }
    }
}
