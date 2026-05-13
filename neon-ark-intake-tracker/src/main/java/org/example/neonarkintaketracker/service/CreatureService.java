package org.example.neonarkintaketracker.service;

import org.example.neonarkintaketracker.dto.*;
import org.example.neonarkintaketracker.entity.Creature;
import org.example.neonarkintaketracker.entity.Habitat;
import org.example.neonarkintaketracker.repository.CreatureRepository;
import org.example.neonarkintaketracker.repository.FeedingScheduleRepository;
import org.example.neonarkintaketracker.repository.HabitatRepository;
import org.example.neonarkintaketracker.repository.ObservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

@Service
public class CreatureService {

    private final CreatureRepository creatureRepository;
    private final HabitatRepository habitatRepository;
    private final ObservationRepository observationRepository;
    private final FeedingScheduleRepository feedingScheduleRepository;

    public CreatureService(CreatureRepository creatureRepository,
                           HabitatRepository habitatRepository,
                           ObservationRepository observationRepository,
                           FeedingScheduleRepository feedingScheduleRepository) {
        this.creatureRepository      = creatureRepository;
        this.habitatRepository       = habitatRepository;
        this.observationRepository   = observationRepository;
        this.feedingScheduleRepository = feedingScheduleRepository;
    }

    // ── GET all (includes REMOVED for full inventory) ─────────────────────────
    public List<CreatureResponse> getAllCreatures() {
        return creatureRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── GET one by id ─────────────────────────────────────────────────────────
    public CreatureResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    // ── POST create ───────────────────────────────────────────────────────────
    public CreatureResponse create(CreatureRequest req) {
        Habitat habitat = habitatRepository.findById(req.habitatId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Habitat not found with id: " + req.habitatId()));

        Creature creature = new Creature();
        creature.setName(req.name());
        creature.setSpecies(req.species());
        creature.setDangerLevel(req.dangerLevel());
        creature.setCondition(req.condition());
        creature.setStatus("ACTIVE");
        creature.setHabitat(habitat);

        return toResponse(creatureRepository.save(creature));
    }

    // ── PUT rename ────────────────────────────────────────────────────────────
    public RenameResponse rename(Long id, RenameRequest req) {
        Creature creature = findOrThrow(id);

        // Check for duplicate name in same habitat
        boolean duplicate = creatureRepository.findAll().stream()
                .anyMatch(c -> !c.getId().equals(id)
                        && c.getHabitat().getId().equals(creature.getHabitat().getId())
                        && c.getName().equalsIgnoreCase(req.newName()));

        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A creature named \"" + req.newName() + "\" already exists in habitat: "
                    + creature.getHabitat().getLocation());
        }

        String oldName = creature.getName();
        creature.setName(req.newName());
        creatureRepository.save(creature);

        return new RenameResponse(
                creature.getId(),
                oldName,
                creature.getName(),
                creature.getHabitat().getLocation()
        );
    }

    // ── DELETE soft delete ────────────────────────────────────────────────────
    public CreatureResponse softDelete(Long id) {
        Creature creature = findOrThrow(id);

        // 409 if creature has active feeding schedules
        boolean hasSchedules = feedingScheduleRepository.findAll().stream()
                .anyMatch(fs -> fs.getCreature().getId().equals(id));

        if (hasSchedules) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot remove creature with active feeding schedules. Remove schedules first.");
        }

        creature.setStatus("REMOVED");
        return toResponse(creatureRepository.save(creature));
    }

    // ── GET creature with all observations ────────────────────────────────────
    public CreatureWithObservationsResponse getWithObservations(Long id) {
        Creature creature = findOrThrow(id);

        List<ObservationResponse> observations = observationRepository
                .findByCreatureIdOrderByObservedAtDesc(id)
                .stream()
                .map(obs -> new ObservationResponse(
                        obs.getId(),
                        obs.getAuthor().getFullName(),
                        obs.getNotes(),
                        obs.getObservedAt()
                ))
                .toList();

        return new CreatureWithObservationsResponse(
                creature.getId(),
                creature.getName(),
                creature.getSpecies(),
                creature.getDangerLevel(),
                creature.getCondition(),
                creature.getStatus(),
                creature.getHabitat().getLocation(),
                creature.getCreatedAt(),
                observations
        );
    }

    // ── GET creatures by feeding time ─────────────────────────────────────────
    public List<CreatureResponse> getByFeedingTime(LocalTime time) {
        return feedingScheduleRepository.findByFeedTime(time)
                .stream()
                .map(fs -> toResponse(fs.getCreature()))
                .distinct()
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Creature findOrThrow(Long id) {
        return creatureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Creature not found with id: " + id));
    }

    private CreatureResponse toResponse(Creature c) {
        return new CreatureResponse(
                c.getId(),
                c.getName(),
                c.getSpecies(),
                c.getDangerLevel(),
                c.getCondition(),
                c.getStatus(),
                c.getHabitat() != null ? c.getHabitat().getLocation() : "Unknown",
                c.getCreatedAt()
        );
    }
}
