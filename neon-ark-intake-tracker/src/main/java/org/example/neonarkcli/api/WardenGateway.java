package org.example.neonarkcli.api;

import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;
import org.example.neonarkcli.model.Warden;

import java.util.List;

// The contract between the service layer and the outside world (HTTP or mock).
// The service depends only on this interface — never on a specific implementation.

public interface WardenGateway {

    /** Retrieve all Warden records (including soft-deleted, for admin view). */
    List<Warden> getAllWardens();

    /**
     * Create a new Warden record.
     * The server assigns the id — the client sends everything else.
     * Returns the created Warden with the server-assigned id populated.
     */
    Warden createWarden(String name, Role role, Status status,
                        String hireDate, String badgeNumber);

    /**
     * Update a specific field on an existing Warden.
     * Only the changed field is sent.
     */
    Warden updateWarden(int id, String field, String newValue);

    /**
     * Soft-delete a Warden by setting is_deleted = true.
     * The record is preserved for audit history.
     */
    void softDeleteWarden(int id);
}
