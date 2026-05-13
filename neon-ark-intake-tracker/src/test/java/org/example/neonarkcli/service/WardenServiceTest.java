package org.example.neonarkcli.service;

import org.example.neonarkcli.api.WardenGateway;
import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;
import org.example.neonarkcli.model.Warden;

import java.util.List;

/**
 * WardenServiceTest demonstrates that business rules are testable
 * without running the menu, starting a server, or connecting to a database.
 *
 * This is a plain Java test file (no JUnit dependency required to read/run
 * conceptually). It uses a FakeWardenGateway instead of the real HTTP client
 * or mock client — a simple in-memory fake that records what was called.
 *
 * This mirrors the TDD examples in the course module:
 *   - rejectBlankName()   -> Rule 1: name cannot be blank
 *   - rejectNullRole()    -> Rule 2: role must be a valid enum value
 *   - rejectNullStatus()  -> Rule 3: status must be a valid enum value
 *   - rejectBadDate()     -> Rule 4: hireDate must be YYYY-MM-DD
 *   - rejectBadBadge()    -> Rule 5: badgeNumber must be BDG-XXXX
 *   - acceptValidWarden() -> Happy path: valid input delegates to gateway
 *
 * To run: right-click this file in IntelliJ -> Run 'WardenServiceTest.main()'
 */
public class WardenServiceTest {

    // ── Fake gateway — records calls, no HTTP, no CSV ─────────────────────────

    static class FakeWardenGateway implements WardenGateway {

        boolean createCalled = false;
        String  lastNameSent = null;

        @Override
        public List<Warden> getAllWardens() {
            return List.of();
        }

        @Override
        public Warden createWarden(String name, Role role, Status status,
                                    String hireDate, String badgeNumber) {
            createCalled = true;
            lastNameSent = name;
            return new Warden(9001, name, role, status, hireDate, badgeNumber, false);
        }

        @Override
        public Warden updateWarden(int id, String field, String newValue) {
            return new Warden();
        }

        @Override
        public void softDeleteWarden(int id) {}
    }

    // ── Test runner ───────────────────────────────────────────────────────────

    public static void main(String[] args) {
        int passed = 0;
        int failed = 0;

        System.out.println();
        System.out.println("  =========================================================");
        System.out.println("          WardenService — Unit Tests                        ");
        System.out.println("  =========================================================");
        System.out.println();

        // Test 1: blank name rejected
        try {
            FakeWardenGateway fake = new FakeWardenGateway();
            WardenService service = new WardenService(fake);
            service.createWarden("   ", "SECURITY", "ACTIVE", "2024-01-01", "BDG-9001");
            System.out.println("  [FAIL] rejectBlankName -- expected exception, got none");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] rejectBlankName -- \"" + e.getMessage() + "\"");
            passed++;
        }

        // Test 2: invalid role rejected
        try {
            FakeWardenGateway fake = new FakeWardenGateway();
            WardenService service = new WardenService(fake);
            service.createWarden("Alex", "WIZARD", "ACTIVE", "2024-01-01", "BDG-9002");
            System.out.println("  [FAIL] rejectInvalidRole -- expected exception, got none");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] rejectInvalidRole -- \"" + e.getMessage() + "\"");
            passed++;
        }

        // Test 3: invalid status rejected
        try {
            FakeWardenGateway fake = new FakeWardenGateway();
            WardenService service = new WardenService(fake);
            service.createWarden("Alex", "SECURITY", "SLEEPING", "2024-01-01", "BDG-9003");
            System.out.println("  [FAIL] rejectInvalidStatus -- expected exception, got none");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] rejectInvalidStatus -- \"" + e.getMessage() + "\"");
            passed++;
        }

        // Test 4: bad date format rejected
        try {
            FakeWardenGateway fake = new FakeWardenGateway();
            WardenService service = new WardenService(fake);
            service.createWarden("Alex", "SECURITY", "ACTIVE", "01/01/2024", "BDG-9004");
            System.out.println("  [FAIL] rejectBadDateFormat -- expected exception, got none");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] rejectBadDateFormat -- \"" + e.getMessage() + "\"");
            passed++;
        }

        // Test 5: bad badge format rejected
        try {
            FakeWardenGateway fake = new FakeWardenGateway();
            WardenService service = new WardenService(fake);
            service.createWarden("Alex", "SECURITY", "ACTIVE", "2024-01-01", "4501");
            System.out.println("  [FAIL] rejectBadBadgeFormat -- expected exception, got none");
            failed++;
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASS] rejectBadBadgeFormat -- \"" + e.getMessage() + "\"");
            passed++;
        }

        // Test 6: valid input delegates to gateway
        try {
            FakeWardenGateway fake = new FakeWardenGateway();
            WardenService service = new WardenService(fake);
            Warden result = service.createWarden(
                    "Jordan Reyes", "CARETAKER", "ACTIVE", "2024-03-15", "BDG-9005");

            if (!fake.createCalled) {
                throw new AssertionError("Expected gateway.createWarden() to be called");
            }
            if (result == null) {
                throw new AssertionError("Expected a non-null Warden to be returned");
            }
            System.out.println("  [PASS] acceptValidWarden -- gateway called, ID=" + result.getId());
            passed++;
        } catch (AssertionError | IllegalArgumentException e) {
            System.out.println("  [FAIL] acceptValidWarden -- " + e.getMessage());
            failed++;
        }

        // Test 7: role validation via enum (case-insensitive)
        try {
            Role role = Role.fromString("caretaker");
            if (role != Role.CARETAKER) throw new AssertionError("Expected CARETAKER");
            System.out.println("  [PASS] roleEnumCaseInsensitive -- \"caretaker\" -> CARETAKER");
            passed++;
        } catch (AssertionError e) {
            System.out.println("  [FAIL] roleEnumCaseInsensitive -- " + e.getMessage());
            failed++;
        }

        // Test 8: invalid role returns null from enum
        try {
            Role role = Role.fromString("WIZARD");
            if (role != null) throw new AssertionError("Expected null for invalid role");
            System.out.println("  [PASS] roleEnumRejectsInvalid -- \"WIZARD\" -> null");
            passed++;
        } catch (AssertionError e) {
            System.out.println("  [FAIL] roleEnumRejectsInvalid -- " + e.getMessage());
            failed++;
        }

        // Summary
        System.out.println();
        System.out.println("  ---------------------------------------------------------");
        System.out.printf ("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("  =========================================================");
        System.out.println();
    }
}
