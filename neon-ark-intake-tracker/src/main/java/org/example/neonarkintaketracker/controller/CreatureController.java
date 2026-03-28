package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.dto.CreatureRequest;
import org.example.neonarkintaketracker.dto.CreatureResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.example.neonarkintaketracker.service.CreatureService;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/creatures")
public class CreatureController {

    private final CreatureService service;

    public CreatureController(CreatureService service) {
        this.service = service;
    }

    // GET /api/creatures
    @GetMapping
    public ResponseEntity<List<CreatureResponse>> listAll() {
        return ResponseEntity.ok(service.getAllCreatures());
    }

    // GET /api/creatures/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CreatureResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // POST /api/creatures
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatureResponse create(@Valid @RequestBody CreatureRequest req) {
        return service.create(req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
}

