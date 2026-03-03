package model;

import java.util.UUID;

/**
 * Represents a scheduled appointment linked to a patient by ID.
 */
public class Appointment {

    private final String id;
    private String patientId;
    private String patientName;   // denormalized for display convenience
    private String date;          // "YYYY-MM-DD"
    private String time;          // "HH:MM"
    private String type;          // e.g. "Check-up", "Follow-up", "Consultation"
    private String doctor;
    private String notes;
    private String status;        // "Scheduled", "Completed", "Cancelled"

    public Appointment(String id, String patientId, String patientName,
                       String date, String time, String type,
                       String doctor, String notes, String status) {
        this.id          = id;
        this.patientId   = patientId;
        this.patientName = patientName;
        this.date        = date;
        this.time        = time;
        this.type        = type;
        this.doctor      = doctor;
        this.notes       = notes;
        this.status      = status;
    }

    public Appointment(String patientId, String patientName,
                       String date, String time, String type,
                       String doctor, String notes, String status) {
        this(UUID.randomUUID().toString(), patientId, patientName,
             date, time, type, doctor, notes, status);
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getPatientId()   { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDate()        { return date; }
    public String getTime()        { return time; }
    public String getType()        { return type; }
    public String getDoctor()      { return doctor; }
    public String getNotes()       { return notes; }
    public String getStatus()      { return status; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setPatientId(String v)   { this.patientId = v; }
    public void setPatientName(String v) { this.patientName = v; }
    public void setDate(String v)        { this.date = v; }
    public void setTime(String v)        { this.time = v; }
    public void setType(String v)        { this.type = v; }
    public void setDoctor(String v)      { this.doctor = v; }
    public void setNotes(String v)       { this.notes = v; }
    public void setStatus(String v)      { this.status = v; }
}
