package org.example.neonarkintaketracker.controller;

import jakarta.validation.Valid;
import org.example.neonarkintaketracker.dto.*;
import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CreatureController {

    private final CreatureService service;

    public CreatureController(CreatureService service) {
        this.service = service;
    }

    // 1. GET /api/creatures — list ALL creatures (including REMOVED)
    @GetMapping("/api/creatures")
    public ResponseEntity<List<CreatureResponse>> listAll() {
        return ResponseEntity.ok(service.getAllCreatures());
    }

    // 2. GET /api/creatures/{id} — get ONE creature
    @GetMapping("/api/creatures/{id}")
    public ResponseEntity<CreatureResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // 3. POST /api/creatures — register new creature
    @PostMapping("/api/creatures")
    @ResponseStatus(HttpStatus.CREATED)
    public CreatureResponse create(@Valid @RequestBody CreatureRequest req) {
        return service.create(req);
    }

    // 4. PUT /api/creatures/{id}/name — rename creature
    @PutMapping("/api/creatures/{id}/name")
    public ResponseEntity<RenameResponse> rename(@PathVariable Long id,
                                                  @Valid @RequestBody RenameRequest req) {
        return ResponseEntity.ok(service.rename(id, req));
    }

    // 5. DELETE /api/creatures/{id} — soft delete creature
    @DeleteMapping("/api/creatures/{id}")
    public ResponseEntity<Map<String, Object>> softDelete(@PathVariable Long id) {
        CreatureResponse updated = service.softDelete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Creature \"" + updated.name() + "\" has been removed from active operations.");
        response.put("creature", updated);
        return ResponseEntity.ok(response);
    }

    // 6. GET /api/creatures/{id}/observations — creature + all observations
    @GetMapping("/api/creatures/{id}/observations")
    public ResponseEntity<CreatureWithObservationsResponse> getWithObservations(@PathVariable Long id) {
        return ResponseEntity.ok(service.getWithObservations(id));
    }

    // 7. GET /api/feedings?time=HH:MM — find creatures by feeding time
    @GetMapping("/api/feedings")
    public ResponseEntity<Map<String, Object>> getByFeedingTime(@RequestParam String time) {
        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(time);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid time format. Use HH:MM (example: 08:00)");
        }

        List<CreatureResponse> creatures = service.getByFeedingTime(parsedTime);
        Map<String, Object> response = new HashMap<>();
        response.put("time", time);
        response.put("creatures", creatures);
        response.put("message", creatures.isEmpty()
                ? "No creatures require feeding at " + time
                : creatures.size() + " creature(s) require feeding at " + time);
        return ResponseEntity.ok(response);
    }

    // Validation error handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}
