package org.example.neonarkintaketracker.repository;

import org.example.neonarkintaketracker.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findByCreatureIdOrderByObservedAtDesc(Long creatureId);
}
