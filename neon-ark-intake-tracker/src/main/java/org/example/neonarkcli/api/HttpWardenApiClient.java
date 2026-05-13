package org.example.neonarkcli.api;

import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;
import org.example.neonarkcli.model.Warden;

import java.util.List;

// Real HTTP adapter. In a deployed system this would make actual HTTP calls.
// For this project, each method prints the request that WOULD be sent.

public class HttpWardenApiClient implements WardenGateway {

    private final String baseUrl;

    public HttpWardenApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    // ── GET all wardens ───────────────────────────────────────────────────────

    @Override
    public List<Warden> getAllWardens() {
        // Real system: HTTP GET baseUrl + "/api/wardens"
        // Real system: parse JSON array -> List<Warden>
        // Simulated: returns empty list; MockWardenApiClient provides seed data
        System.out.println();
        System.out.println("  WOULD SEND:  GET   " + baseUrl + "/api/wardens");
        System.out.println("  DESCRIPTION: Retrieve all Warden records from the server.");
        System.out.println("               Server filters is_deleted based on query param.");
        System.out.println();
        return List.of(); // placeholder — MockWardenApiClient handles real display
    }

    // ── POST create warden ────────────────────────────────────────────────────

    @Override
    public Warden createWarden(String name, Role role, Status status,
                                String hireDate, String badgeNumber) {
        System.out.println();
        System.out.println("  WOULD SEND:  POST   " + baseUrl + "/api/wardens");
        System.out.println("  DESCRIPTION: Create a new Warden record. The server assigns");
        System.out.println("               warden_id and created_at. is_deleted defaults to false.");
        System.out.println();
        System.out.println("  PAYLOAD (JSON)");
        System.out.println("  {");
        System.out.printf ("    \"name\"       : \"%s\",%n", name);
        System.out.printf ("    \"role\"       : \"%s\",%n", role.name());
        System.out.printf ("    \"status\"     : \"%s\",%n", status.name());
        System.out.printf ("    \"hireDate\"   : \"%s\",%n", hireDate);
        System.out.printf ("    \"badgeNumber\": \"%s\"%n",  badgeNumber);
        System.out.println("  }");

        // Real system: parse JSON response -> Warden with server-assigned id
        // Simulated: return a placeholder Warden with a fake id
        return new Warden(9999, name, role, status, hireDate, badgeNumber, false);
    }

    // ── PUT update warden ─────────────────────────────────────────────────────

    @Override
    public Warden updateWarden(int id, String field, String newValue) {
        System.out.println();
        System.out.println("  WOULD SEND:  PUT   " + baseUrl + "/api/wardens/" + id);
        System.out.println("  DESCRIPTION: Update a specific field on an existing Warden.");
        System.out.println("               Only the changed field is included in the payload.");
        System.out.println();
        System.out.println("  PAYLOAD (JSON)");
        System.out.println("  {");
        System.out.printf ("    \"%s\": \"%s\"%n", field, newValue);
        System.out.println("  }");

        return new Warden(); // placeholder
    }

    // ── DELETE soft-delete warden ─────────────────────────────────────────────

    @Override
    public void softDeleteWarden(int id) {
        System.out.println();
        System.out.println("  WOULD SEND:  DELETE   " + baseUrl + "/api/wardens/" + id);
        System.out.println("  DESCRIPTION: Soft-delete the Warden. Sets is_deleted = true.");
        System.out.println("               Record is preserved in the database for audit history.");
        System.out.println("               This Warden will no longer appear in active rosters.");
        System.out.println();
        System.out.println("  No request body required. The path parameter identifies the target.");
    }
}
