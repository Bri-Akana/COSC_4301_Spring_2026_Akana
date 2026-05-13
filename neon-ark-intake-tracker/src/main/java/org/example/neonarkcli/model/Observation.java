package org.example.neonarkcli.model;

// Data model for a creature observation record.
public class Observation {
    private long   id;
    private String authorName;
    private String notes;
    private String observedAt;

    public Observation() {}

    public Observation(long id, String authorName, String notes, String observedAt) {
        this.id         = id;
        this.authorName = authorName;
        this.notes      = notes;
        this.observedAt = observedAt;
    }

    public long   getId()         { return id;         }
    public String getAuthorName() { return authorName; }
    public String getNotes()      { return notes;      }
    public String getObservedAt() { return observedAt; }
}
