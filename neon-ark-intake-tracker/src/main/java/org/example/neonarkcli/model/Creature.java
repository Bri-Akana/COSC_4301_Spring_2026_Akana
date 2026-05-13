package org.example.neonarkcli.model;

// Data model for a Neon Ark creature. Stores data only — no rules, no HTTP.
public class Creature {
    private long   id;
    private String name;
    private String species;
    private String dangerLevel;
    private String condition;
    private String status;
    private String habitatName;
    private String createdAt;

    public Creature() {}

    public Creature(long id, String name, String species, String dangerLevel,
                    String condition, String status, String habitatName, String createdAt) {
        this.id          = id;
        this.name        = name;
        this.species     = species;
        this.dangerLevel = dangerLevel;
        this.condition   = condition;
        this.status      = status;
        this.habitatName = habitatName;
        this.createdAt   = createdAt;
    }

    public long   getId()          { return id;          }
    public String getName()        { return name;        }
    public String getSpecies()     { return species;     }
    public String getDangerLevel() { return dangerLevel; }
    public String getCondition()   { return condition;   }
    public String getStatus()      { return status;      }
    public String getHabitatName() { return habitatName; }
    public String getCreatedAt()   { return createdAt;   }
}
