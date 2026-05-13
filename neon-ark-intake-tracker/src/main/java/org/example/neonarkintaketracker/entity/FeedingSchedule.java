package org.example.neonarkintaketracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "feeding_schedules")
public class FeedingSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creature_id", nullable = false)
    private Creature creature;

    @Column(name = "feed_time", nullable = false)
    private LocalTime feedTime;

    @Column(name = "food_type", nullable = false, length = 120)
    private String foodType;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
