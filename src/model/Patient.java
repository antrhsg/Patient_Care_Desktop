package model;

import java.util.UUID;

/**
 * Immutable-friendly model representing a patient record.
 * Uses a builder pattern for clean construction.
 */
public class Patient {

    private final String id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;   // stored as "YYYY-MM-DD"
    private String gender;
    private String phone;
    private String email;
    private String bloodType;
    private String allergies;
    private String notes;

    /** Full constructor — used when loading from persistence. */
    public Patient(String id, String firstName, String lastName, String dateOfBirth,
                   String gender, String phone, String email,
                   String bloodType, String allergies, String notes) {
        this.id          = id;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender      = gender;
        this.phone       = phone;
        this.email       = email;
        this.bloodType   = bloodType;
        this.allergies   = allergies;
        this.notes       = notes;
    }

    /** Convenience constructor — generates a new UUID for new patients. */
    public Patient(String firstName, String lastName, String dateOfBirth,
                   String gender, String phone, String email,
                   String bloodType, String allergies, String notes) {
        this(UUID.randomUUID().toString(), firstName, lastName, dateOfBirth,
             gender, phone, email, bloodType, allergies, notes);
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getFullName()    { return firstName + " " + lastName; }
    public String getDateOfBirth() { return dateOfBirth; }
    public String getGender()      { return gender; }
    public String getPhone()       { return phone; }
    public String getEmail()       { return email; }
    public String getBloodType()   { return bloodType; }
    public String getAllergies()   { return allergies; }
    public String getNotes()       { return notes; }

    // ── Setters (for edit operations) ────────────────────────────────────────
    public void setFirstName(String v)   { this.firstName = v; }
    public void setLastName(String v)    { this.lastName = v; }
    public void setDateOfBirth(String v) { this.dateOfBirth = v; }
    public void setGender(String v)      { this.gender = v; }
    public void setPhone(String v)       { this.phone = v; }
    public void setEmail(String v)       { this.email = v; }
    public void setBloodType(String v)   { this.bloodType = v; }
    public void setAllergies(String v)   { this.allergies = v; }
    public void setNotes(String v)       { this.notes = v; }

    @Override
    public String toString() {
        return getFullName() + " (DOB: " + dateOfBirth + ")";
    }
}
