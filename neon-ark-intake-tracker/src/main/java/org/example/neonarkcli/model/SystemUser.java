package org.example.neonarkcli.model;

// Data model for a Neon Ark system user.
public class SystemUser {
    private long   id;
    private String fullName;
    private String email;
    private String phone;
    private String role;

    public SystemUser() {}

    public SystemUser(long id, String fullName, String email, String phone, String role) {
        this.id       = id;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.role     = role;
    }

    public long   getId()       { return id;       }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email;    }
    public String getPhone()    { return phone;    }
    public String getRole()     { return role;     }
}
