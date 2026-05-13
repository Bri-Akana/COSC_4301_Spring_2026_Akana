package org.example.neonarkcli.model;

import org.example.neonarkcli.domain.Role;
import org.example.neonarkcli.domain.Status;

/**
 * Data model for a Neon Ark Warden. Stores data only
 *
 * Fields mirror the wardens table defined in the database design project:
 *   id          — server-assigned unique identity (client never invents this)
 *   name        — employee full name (required)
 *   role        — functional role, typed as Role enum (not a raw String)
 *   status      — employment lifecycle state, typed as Status enum
 *   hireDate    — date onboarded, YYYY-MM-DD format
 *   badgeNumber — unique badge identifier, format BDG-XXXX
 *   isDeleted   — soft-delete flag; true = removed from active rosters
 */
public class Warden {

    private int    id;
    private String name;
    private Role   role;
    private Status status;
    private String hireDate;
    private String badgeNumber;
    private boolean isDeleted;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Warden() {}

    public Warden(int id, String name, Role role, Status status,
                  String hireDate, String badgeNumber, boolean isDeleted) {
        this.id          = id;
        this.name        = name;
        this.role        = role;
        this.status      = status;
        this.hireDate    = hireDate;
        this.badgeNumber = badgeNumber;
        this.isDeleted   = isDeleted;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int     getId()          { return id;          }
    public String  getName()        { return name;        }
    public Role    getRole()        { return role;        }
    public Status  getStatus()      { return status;      }
    public String  getHireDate()    { return hireDate;    }
    public String  getBadgeNumber() { return badgeNumber; }
    public boolean isDeleted()      { return isDeleted;   }

    public void setId(int id)                 { this.id          = id;         }
    public void setName(String name)          { this.name        = name;       }
    public void setRole(Role role)            { this.role        = role;       }
    public void setStatus(Status status)      { this.status      = status;     }
    public void setHireDate(String hireDate)  { this.hireDate    = hireDate;   }
    public void setBadgeNumber(String badge)  { this.badgeNumber = badge;      }
    public void setDeleted(boolean isDeleted) { this.isDeleted   = isDeleted;  }
}
