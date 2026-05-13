package org.example.neonarkcli.menu;

import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;
import org.example.neonarkcli.model.Warden;
import org.example.neonarkcli.service.WardenService;

import java.util.List;
import java.util.Scanner;

// User interaction layer. Prints menus, reads input, delegates to WardenService.
// Does not validate rules and does not know about HTTP.

public class WardenMenu {

    private final WardenService service;
    private final Scanner scanner;

    public WardenMenu(WardenService service, Scanner scanner) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();

            switch (choice) {
                case "1" -> handleAddWarden();
                case "2" -> showViewMenu();
                case "3" -> showUpdateMenu();
                case "4" -> showCertMenu();
                case "5" -> showDeactivateMenu();
                case "6" -> {
                    System.out.println("  Logging out of Neon Ark Admin Console.");
                    System.out.println("  All session data has been discarded.");
                    System.out.println("  Stay safe out there.");
                    System.out.println();
                    running = false;
                    return;
                }
                default -> System.out.println("  Invalid selection. Please enter a number from 1 to 6.");
            }

            System.out.println();
            System.out.println("  Press ENTER to return to the Main Menu...");
            scanner.nextLine();
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("  =========================================================");
        System.out.println("          NEON ARK -- ADMIN WARDEN ONBOARDING CONSOLE      ");
        System.out.println("  =========================================================");
        System.out.println();
        System.out.println("  [ MAIN MENU ]");
        System.out.println("  ---------------------------------------------------------");
        System.out.println("  1. Add New Warden");
        System.out.println("  2. View Wardens");
        System.out.println("  3. Update Warden");
        System.out.println("  4. Manage Certifications");
        System.out.println("  5. Deactivate / Terminate Warden");
        System.out.println("  6. Exit");
        System.out.println("  ---------------------------------------------------------");
        System.out.print("  Select an option: ");
    }

    private void handleAddWarden() {
        System.out.println("  -- Add New Warden --------------------------------------------");
        System.out.println("  Complete all required fields. Type 'cancel' at any field to exit.");
        System.out.println();

        try {
            String name = promptValidated(
                    "  Warden Name           : ", null,
                    v -> service.validateName(v));

            String roleInput = promptValidated(
                    "  Role                  : ", "Options: " + Role.options(),
                    v -> service.validateRole(v));

            String statusInput = promptValidated(
                    "  Status                : ", "Options: " + Status.options(),
                    v -> service.validateStatus(v));

            String hireDate = promptValidated(
                    "  Hire Date             : ", "Format: YYYY-MM-DD",
                    v -> service.validateHireDate(v));

            String badge = promptValidated(
                    "  Badge Number          : ", "Format: BDG-XXXX (example: BDG-4501)",
                    v -> service.validateBadge(v, true));

            Warden created = service.createWarden(name, roleInput, statusInput, hireDate, badge);

            System.out.println();
            System.out.println("  -- Simulated Outbound Request --------------------------------");
            System.out.println();
            System.out.println("  WOULD SEND:  POST   /api/wardens");
            System.out.println("  DESCRIPTION: Create a new Warden record. Server assigns warden_id.");
            System.out.println();
            System.out.println("  PAYLOAD (JSON)");
            System.out.println("  {");
            System.out.printf ("    \"name\"       : \"%s\",%n", created.getName());
            System.out.printf ("    \"role\"       : \"%s\",%n", created.getRole().name());
            System.out.printf ("    \"status\"     : \"%s\",%n", created.getStatus().name());
            System.out.printf ("    \"hireDate\"   : \"%s\",%n", created.getHireDate());
            System.out.printf ("    \"badgeNumber\": \"%s\"%n",  created.getBadgeNumber());
            System.out.println("  }");
            System.out.println();
            System.out.println("  -- Simulated Server Response ---------------------------------");
            System.out.println();
            System.out.println("  HTTP 201 CREATED");
            System.out.println("  {");
            System.out.printf ("    \"wardenId\"   : %d,%n",     created.getId());
            System.out.printf ("    \"name\"       : \"%s\",%n", created.getName());
            System.out.printf ("    \"role\"       : \"%s\",%n", created.getRole().name());
            System.out.printf ("    \"status\"     : \"%s\",%n", created.getStatus().name());
            System.out.printf ("    \"hireDate\"   : \"%s\",%n", created.getHireDate());
            System.out.printf ("    \"badgeNumber\": \"%s\",%n", created.getBadgeNumber());
            System.out.println("    \"isDeleted\"  : false");
            System.out.println("  }");
            System.out.println();
            System.out.println("  Warden \"" + created.getName() + "\" created with ID " + created.getId() + ".");
            System.out.println("  This record exists in session memory only and will not persist.");

        } catch (CancelException e) {
            System.out.println();
            System.out.println("  Add Warden cancelled. No record was created.");
        }
    }

    private void showViewMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("  =========================================================");
            System.out.println("                      VIEW WARDENS                         ");
            System.out.println("  =========================================================");
            System.out.println("  1. View All Wardens");
            System.out.println("  2. View Active Wardens Only  (simulated)");
            System.out.println("  3. Search Warden by ID       (simulated)");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ---------------------------------------------------------");
            System.out.print("  Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> displayWardenTable(service.listWardens());
                case "2" -> simulateViewActive();
                case "3" -> simulateSearchById();
                case "0" -> inMenu = false;
                default  -> System.out.println("  Invalid selection. Please enter 0, 1, 2, or 3.");
            }
            if (inMenu) {
                System.out.println();
                System.out.println("  Press ENTER to continue...");
                scanner.nextLine();
                System.out.println();
            }
        }
    }

    private void displayWardenTable(List<Warden> wardens) {
        System.out.println();
        System.out.println("  Loading Warden roster from database snapshot...");
        System.out.println();

        if (wardens.isEmpty()) {
            System.out.println("  No Warden records found.");
            return;
        }

        String fmt     = "  %-6s  %-22s  %-16s  %-10s  %-12s  %-10s  %-9s%n";
        String divider = "  " + "-".repeat(100);

        System.out.printf(fmt, "ID", "NAME", "ROLE", "STATUS", "HIRE DATE", "BADGE NO.", "DELETED");
        System.out.println(divider);

        for (Warden w : wardens) {
            System.out.printf(fmt,
                    w.getId(),
                    w.getName(),
                    w.getRole().name(),
                    w.getStatus().name(),
                    w.getHireDate(),
                    w.getBadgeNumber(),
                    w.isDeleted() ? "YES" : "no"
            );
        }

        System.out.println(divider);
        System.out.printf("  Total records: %d%n", wardens.size());
        System.out.println();
        System.out.println("  NOTE: Records marked DELETED are soft-deleted.");
        System.out.println("        They remain for audit purposes. Active rosters filter these out.");
    }

    private void simulateViewActive() {
        System.out.println();
        System.out.println("  -- View Active Wardens (Simulated) ---------------------------");
        System.out.println();
        System.out.println("  ACTION      : Retrieve all Wardens where status = ACTIVE");
        System.out.println("                and is_deleted = false.");
        System.out.println();
        System.out.println("  WOULD SEND:  GET   /api/wardens?status=ACTIVE");
        System.out.println("  DESCRIPTION: Returns only Active, non-deleted Warden records.");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- filtered list of Active Warden records");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS -- 12 Active Warden records would be returned.");
    }

    private void simulateSearchById() {
        System.out.println();
        System.out.println("  -- Search Warden by ID (Simulated) ---------------------------");
        System.out.println();
        System.out.println("  ACTION      : Look up a single Warden record by their unique ID.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId   [required] -- the numeric ID of the Warden");
        System.out.println();
        System.out.println("  VALIDATION THAT WOULD OCCUR:");
        System.out.println("    - wardenId must be a positive integer");
        System.out.println("    - wardenId must exist in the database");
        System.out.println();
        System.out.println("  WOULD SEND:  GET   /api/wardens/{wardenId}");
        System.out.println("  DESCRIPTION: Returns the full Warden record for the given ID.");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- single Warden record");
        System.out.println("    HTTP 404    -- wardenId does not exist");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
    }

    private void showUpdateMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("  =========================================================");
            System.out.println("                     UPDATE WARDEN                         ");
            System.out.println("  =========================================================");
            System.out.println("  1. Update Name");
            System.out.println("  2. Update Role");
            System.out.println("  3. Update Employment Status");
            System.out.println("  4. Update Hire Date");
            System.out.println("  5. Update Badge Number");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ---------------------------------------------------------");
            System.out.print("  Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> printSimulatedUpdate("name",
                        "- Cannot be blank\n    - Maximum 120 characters",
                        "{ \"name\": \"Jordan Reyes\" }");
                case "2" -> printSimulatedUpdate("role",
                        "- Must be one of: " + Role.options(),
                        "{ \"role\": \"SECURITY\" }");
                case "3" -> printSimulatedUpdate("status",
                        "- Must be one of: " + Status.options(),
                        "{ \"status\": \"INACTIVE\" }");
                case "4" -> printSimulatedUpdate("hireDate",
                        "- Must follow YYYY-MM-DD format",
                        "{ \"hireDate\": \"2025-06-01\" }");
                case "5" -> printSimulatedUpdate("badgeNumber",
                        "- Must follow BDG-XXXX format\n    - Must be unique across all Wardens",
                        "{ \"badgeNumber\": \"BDG-4501\" }");
                case "0" -> inMenu = false;
                default  -> System.out.println("  Invalid selection. Please enter 0 through 5.");
            }
            if (inMenu) {
                System.out.println();
                System.out.println("  Press ENTER to continue...");
                scanner.nextLine();
                System.out.println();
            }
        }
    }

    private void printSimulatedUpdate(String field, String validation, String payload) {
        System.out.println();
        System.out.println("  -- Update " + field + " (Simulated) -----------------------------------");
        System.out.println();
        System.out.println("  ACTION      : Update the " + field + " on an existing Warden record.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId   [required] -- the ID of the Warden to update");
        System.out.println("    " + field + "     [required] -- the new value");
        System.out.println();
        System.out.println("  VALIDATION THAT WOULD OCCUR:");
        System.out.println("    - wardenId must exist and must not be soft-deleted");
        System.out.println("    - " + validation);
        System.out.println();
        System.out.println("  WOULD SEND:  PUT   /api/wardens/{wardenId}");
        System.out.println("  DESCRIPTION: Updates the " + field + " field on the specified Warden.");
        System.out.println();
        System.out.println("  PAYLOAD (JSON)");
        System.out.println("  " + payload);
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- updated Warden record returned");
        System.out.println("    HTTP 404    -- wardenId does not exist");
        System.out.println("    HTTP 400    -- validation failure");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
    }

    private void showCertMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("  =========================================================");
            System.out.println("                MANAGE CERTIFICATIONS                      ");
            System.out.println("  =========================================================");
            System.out.println("  1. Add Certification to Warden");
            System.out.println("  2. View Certifications for a Warden");
            System.out.println("  3. Revoke a Certification");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ---------------------------------------------------------");
            System.out.print("  Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> simulateAddCertification();
                case "2" -> simulateViewCertifications();
                case "3" -> simulateRevokeCertification();
                case "0" -> inMenu = false;
                default  -> System.out.println("  Invalid selection. Please enter 0, 1, 2, or 3.");
            }
            if (inMenu) {
                System.out.println();
                System.out.println("  Press ENTER to continue...");
                scanner.nextLine();
                System.out.println();
            }
        }
    }

    private void simulateAddCertification() {
        System.out.println();
        System.out.println("  -- Add Certification (Simulated) -----------------------------");
        System.out.println();
        System.out.println("  ACTION      : Associate a new certification with an existing Warden.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId       [required] -- ID of the Warden");
        System.out.println("    certName       [required] -- name of the certification (max 120 chars)");
        System.out.println("    earnedDate     [required] -- date earned, format YYYY-MM-DD");
        System.out.println("    expirationDate [optional] -- expiry date, format YYYY-MM-DD");
        System.out.println();
        System.out.println("  VALIDATION THAT WOULD OCCUR:");
        System.out.println("    - wardenId must exist and not be soft-deleted");
        System.out.println("    - certName cannot be blank");
        System.out.println("    - earnedDate must be a valid YYYY-MM-DD date");
        System.out.println("    - expirationDate, if provided, must be after earnedDate");
        System.out.println();
        System.out.println("  WOULD SEND:  POST   /api/wardens/{wardenId}/certifications");
        System.out.println("  DESCRIPTION: Create a new certification record and associate it");
        System.out.println("               with an existing Warden. The Warden ID is supplied");
        System.out.println("               as a path parameter to clearly indicate ownership.");
        System.out.println();
        System.out.println("  PAYLOAD (JSON)");
        System.out.println("  {");
        System.out.println("    \"name\"           : \"Rift Safety Level 1\",");
        System.out.println("    \"earnedDate\"     : \"2026-02-03\",");
        System.out.println("    \"expirationDate\" : \"2027-02-03\"");
        System.out.println("  }");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 201 CREATED -- new certification record with cert ID");
        System.out.println("    HTTP 404         -- wardenId does not exist");
        System.out.println("    HTTP 400         -- earnedDate missing or invalid format");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
    }

    private void simulateViewCertifications() {
        System.out.println();
        System.out.println("  -- View Certifications for a Warden (Simulated) --------------");
        System.out.println();
        System.out.println("  ACTION      : Retrieve all certifications linked to one Warden.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId   [required] -- the ID of the Warden to look up");
        System.out.println();
        System.out.println("  WOULD SEND:  GET   /api/wardens/{wardenId}/certifications");
        System.out.println("  DESCRIPTION: Returns all certification records owned by this Warden.");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- array of certification objects");
        System.out.println("    HTTP 404    -- wardenId does not exist");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
        System.out.println("  [");
        System.out.println("    { \"certId\": 301, \"name\": \"Rift Safety Level 1\",");
        System.out.println("      \"earnedDate\": \"2025-04-10\", \"expirationDate\": \"2026-04-10\" },");
        System.out.println("    { \"certId\": 302, \"name\": \"Xenobiology Handling Basics\",");
        System.out.println("      \"earnedDate\": \"2024-11-01\", \"expirationDate\": null }");
        System.out.println("  ]");
    }

    private void simulateRevokeCertification() {
        System.out.println();
        System.out.println("  -- Revoke Certification (Simulated) --------------------------");
        System.out.println();
        System.out.println("  ACTION      : Mark a certification as revoked for a specific Warden.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId   [required] -- the ID of the Warden");
        System.out.println("    certId     [required] -- the ID of the certification to revoke");
        System.out.println();
        System.out.println("  VALIDATION THAT WOULD OCCUR:");
        System.out.println("    - wardenId must exist");
        System.out.println("    - certId must belong to the specified Warden");
        System.out.println("    - cannot revoke an already-revoked certification");
        System.out.println();
        System.out.println("  WOULD SEND:  PATCH   /api/wardens/{wardenId}/certifications/{certId}/revoke");
        System.out.println("  DESCRIPTION: Sets the certification's revoked flag to true.");
        System.out.println("               Record is preserved for audit history.");
        System.out.println();
        System.out.println("  PAYLOAD (JSON)");
        System.out.println("  { \"revoked\": true }");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- updated certification record");
        System.out.println("    HTTP 404    -- wardenId or certId does not exist");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
    }

    private void showDeactivateMenu() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("  =========================================================");
            System.out.println("             DEACTIVATE / TERMINATE WARDEN                 ");
            System.out.println("  =========================================================");
            System.out.println("  1. Deactivate Warden  (set status to INACTIVE)");
            System.out.println("  2. Terminate Warden   (soft delete -- preserves record)");
            System.out.println("  0. Back to Main Menu");
            System.out.println("  ---------------------------------------------------------");
            System.out.print("  Select an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> simulateDeactivate();
                case "2" -> simulateTerminate();
                case "0" -> inMenu = false;
                default  -> System.out.println("  Invalid selection. Please enter 0, 1, or 2.");
            }
            if (inMenu) {
                System.out.println();
                System.out.println("  Press ENTER to continue...");
                scanner.nextLine();
                System.out.println();
            }
        }
    }

    private void simulateDeactivate() {
        System.out.println();
        System.out.println("  -- Deactivate Warden (Simulated) ----------------------------");
        System.out.println();
        System.out.println("  ACTION      : Set a Warden's status to INACTIVE.");
        System.out.println("                Used for temporary removal from active duty.");
        System.out.println("                The record is NOT deleted.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId   [required] -- the ID of the Warden to deactivate");
        System.out.println();
        System.out.println("  VALIDATION THAT WOULD OCCUR:");
        System.out.println("    - wardenId must exist and not be soft-deleted");
        System.out.println("    - Warden must not already be INACTIVE");
        System.out.println("    - Cannot deactivate a Warden with status DECEASED");
        System.out.println();
        System.out.println("  WOULD SEND:  PATCH   /api/wardens/{wardenId}/status");
        System.out.println("  DESCRIPTION: Updates the Warden's status field to INACTIVE.");
        System.out.println();
        System.out.println("  PAYLOAD (JSON)");
        System.out.println("  { \"status\": \"INACTIVE\" }");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- updated Warden record");
        System.out.println("    HTTP 404    -- wardenId does not exist");
        System.out.println("    HTTP 409    -- Warden is already INACTIVE or DECEASED");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
    }

    private void simulateTerminate() {
        System.out.println();
        System.out.println("  -- Terminate Warden / Soft Delete (Simulated) ---------------");
        System.out.println();
        System.out.println("  ACTION      : Soft-delete a Warden by setting is_deleted = true.");
        System.out.println("                Neon Ark preserves all records for audit and history.");
        System.out.println("                This Warden will NO LONGER appear in active rosters.");
        System.out.println();
        System.out.println("  INPUTS REQUIRED:");
        System.out.println("    wardenId         [required] -- the ID of the Warden to terminate");
        System.out.println("    confirmationCode [required] -- operator must type CONFIRM to proceed");
        System.out.println();
        System.out.println("  VALIDATION THAT WOULD OCCUR:");
        System.out.println("    - wardenId must exist");
        System.out.println("    - Warden must not already be soft-deleted");
        System.out.println("    - Operator must explicitly type CONFIRM -- no accidental terminations");
        System.out.println();
        System.out.println("  WOULD SEND:  DELETE   /api/wardens/{wardenId}");
        System.out.println("  DESCRIPTION: Soft-delete. Sets is_deleted = true on the Warden record.");
        System.out.println("               Record is retained in the database for compliance.");
        System.out.println("               No payload required -- path parameter identifies the target.");
        System.out.println();
        System.out.println("  EXPECTED RESULT:");
        System.out.println("    HTTP 200 OK -- confirmation message");
        System.out.println("    HTTP 404    -- wardenId does not exist");
        System.out.println("    HTTP 409    -- Warden is already soft-deleted");
        System.out.println();
        System.out.println("  RESULT (simulated): SUCCESS");
        System.out.println("  { \"message\": \"Warden terminated and removed from active rosters.\" }");
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private helpers
    // ═════════════════════════════════════════════════════════════════════════

    private String promptFor(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    /**
     * Prompts for a field, validates immediately by calling back into WardenService.
     * Re-prompts only this field on failure. Type 'cancel' to exit the whole form.
     */
    private String promptValidated(String label, String hint,
                                   java.util.function.Consumer<String> rule) {
        while (true) {
            if (hint != null) System.out.println("  " + hint);
            System.out.print(label);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel")) {
                throw new CancelException();
            }

            try {
                rule.accept(input);
                return input;
            } catch (IllegalArgumentException e) {
                System.out.println("  ! " + e.getMessage());
                System.out.println();
            }
        }
    }

    /** Thrown when the operator types 'cancel' during Add New Warden. */
    private static class CancelException extends RuntimeException {}
}
