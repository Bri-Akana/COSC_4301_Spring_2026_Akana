package org.example.neonarkcli.service;

import org.example.neonarkcli.api.MockWardenApiClient;
import org.example.neonarkcli.api.WardenGateway;
import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;
import org.example.neonarkcli.model.Warden;

import java.util.List;

// Business rules layer. Answers the question: "What should happen?"
// All validation lives here — not in the menu, not in the model.
// Depends on WardenGateway interface, not a specific implementation.

public class WardenService {

    private final WardenGateway gateway;

    public WardenService(WardenGateway gateway) {
        this.gateway = gateway;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // listWardens — delegates to gateway, no business rules needed
    // ═════════════════════════════════════════════════════════════════════════

    public List<Warden> listWardens() {
        return gateway.getAllWardens();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // createWarden — validates ALL rules before delegating to the gateway
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Creates a new Warden after enforcing all business rules.
     *
     * Rules enforced here (not in the menu):
     *   1. Name cannot be blank
     *   2. Role must be a valid enum value
     *   3. Status must be a valid enum value
     *   4. HireDate must follow YYYY-MM-DD format
     *   5. BadgeNumber must follow BDG-XXXX format
     *   6. BadgeNumber must be unique
     *
     * @throws IllegalArgumentException with a human-readable message if any rule fails
     */
    public Warden createWarden(String name, String roleInput, String statusInput,
                               String hireDate, String badgeNumber) {

        // Rule 1: name required
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Warden name cannot be blank. Please enter the employee's full name.");
        }
        if (name.trim().length() > 120) {
            throw new IllegalArgumentException(
                    "Warden name cannot exceed 120 characters.");
        }

        // Rule 2: role must be valid enum value
        Role role = Role.fromString(roleInput);
        if (role == null) {
            throw new IllegalArgumentException(
                    "\"" + roleInput + "\" is not a recognized role. " +
                            "Valid options are: " + Role.options());
        }

        // Rule 3: status must be valid enum value
        Status status = Status.fromString(statusInput);
        if (status == null) {
            throw new IllegalArgumentException(
                    "\"" + statusInput + "\" is not a recognized status. " +
                            "Valid options are: " + Status.options());
        }

        // Rule 4: hireDate format YYYY-MM-DD
        validateHireDateInternal(hireDate);

        // Rule 5: badgeNumber format BDG-XXXX
        validateBadgeFormat(badgeNumber);

        // Rule 6: badgeNumber must be unique
        if (gateway instanceof MockWardenApiClient mock) {
            if (mock.badgeExists(badgeNumber)) {
                throw new IllegalArgumentException(
                        "Badge number \"" + badgeNumber + "\" is already assigned to another Warden. " +
                                "Each Warden must have a unique badge number.");
            }
        }

        // All rules passed — delegate to the gateway
        return gateway.createWarden(name, role, status, hireDate, badgeNumber);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // updateWarden — validates id and new value before delegating
    // ═════════════════════════════════════════════════════════════════════════

    public Warden updateWarden(int id, String field, String newValue) {
        if (newValue == null || newValue.isBlank()) {
            throw new IllegalArgumentException(
                    "New value cannot be blank.");
        }

        // If updating role or status, validate against the enum
        if (field.equalsIgnoreCase("role") && Role.fromString(newValue) == null) {
            throw new IllegalArgumentException(
                    "\"" + newValue + "\" is not a valid role. Options: " + Role.options());
        }
        if (field.equalsIgnoreCase("status") && Status.fromString(newValue) == null) {
            throw new IllegalArgumentException(
                    "\"" + newValue + "\" is not a valid status. Options: " + Status.options());
        }
        if (field.equalsIgnoreCase("hireDate")) {
            validateHireDateInternal(newValue);
        }
        if (field.equalsIgnoreCase("badgeNumber")) {
            validateBadgeFormat(newValue);
        }

        return gateway.updateWarden(id, field, newValue);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // softDeleteWarden — validates id before delegating
    // ═════════════════════════════════════════════════════════════════════════

    public void softDeleteWarden(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Warden ID must be a positive number.");
        }
        gateway.softDeleteWarden(id);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Public per-field validators
    // Called by WardenMenu for immediate field-by-field feedback.
    // Rules still live here in the service — the menu just calls them sooner.
    // ═════════════════════════════════════════════════════════════════════════

    public void validateName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException(
                    "Warden name cannot be blank. Please enter the employee's full name.");
        if (name.trim().length() > 120)
            throw new IllegalArgumentException(
                    "Warden name cannot exceed 120 characters.");
    }

    public void validateRole(String roleInput) {
        if (Role.fromString(roleInput) == null)
            throw new IllegalArgumentException(
                    "\"" + roleInput + "\" is not a recognized role. " +
                            "Valid options are: " + Role.options());
    }

    public void validateStatus(String statusInput) {
        if (Status.fromString(statusInput) == null)
            throw new IllegalArgumentException(
                    "\"" + statusInput + "\" is not a recognized status. " +
                            "Valid options are: " + Status.options());
    }

    public void validateHireDate(String hireDate) {
        validateHireDateInternal(hireDate);
    }

    public void validateBadge(String badgeNumber, boolean checkUniqueness) {
        validateBadgeFormat(badgeNumber);
        if (checkUniqueness && gateway instanceof MockWardenApiClient mock) {
            if (mock.badgeExists(badgeNumber))
                throw new IllegalArgumentException(
                        "Badge number \"" + badgeNumber + "\" is already assigned to another Warden. " +
                                "Each Warden must have a unique badge number.");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Private validation helpers (used internally by createWarden + above)
    // ═════════════════════════════════════════════════════════════════════════

    private void validateHireDateInternal(String hireDate) {
        if (hireDate == null || hireDate.isBlank()) {
            throw new IllegalArgumentException(
                    "Hire date is required. Use format YYYY-MM-DD (example: 2024-03-15).");
        }
        if (!hireDate.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException(
                    "\"" + hireDate + "\" is not a valid date format. " +
                            "Please use YYYY-MM-DD (example: 2024-03-15).");
        }
        String[] parts = hireDate.trim().split("-");
        int month = Integer.parseInt(parts[1]);
        int day   = Integer.parseInt(parts[2]);
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "Month \"" + parts[1] + "\" is invalid. Month must be between 01 and 12.");
        }
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException(
                    "Day \"" + parts[2] + "\" is invalid. Day must be between 01 and 31.");
        }
    }

    private void validateBadgeFormat(String badgeNumber) {
        if (badgeNumber == null || badgeNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Badge number is required. Use format BDG-XXXX (example: BDG-4501).");
        }
        if (!badgeNumber.trim().matches("BDG-\\d{4}")) {
            throw new IllegalArgumentException(
                    "\"" + badgeNumber + "\" is not a valid badge format. " +
                            "Badge numbers must follow the pattern BDG-XXXX (example: BDG-4501).");
        }
    }
}