package controller;

import model.Patient;
import util.DataStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller layer for patient operations.
 * Owns the in-memory list and coordinates with DataStore for persistence.
 * Views interact with this class — never with DataStore directly.
 */
public class PatientController {

    private final List<Patient> patients = new ArrayList<>();

    public PatientController() {
        patients.addAll(DataStore.loadPatients());
        if (patients.isEmpty()) seedSampleData();
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void addPatient(Patient p) {
        patients.add(p);
        persist();
    }

    public void updatePatient(String id, Patient updated) {
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equals(id)) {
                Patient existing = patients.get(i);
                existing.setFirstName(updated.getFirstName());
                existing.setLastName(updated.getLastName());
                existing.setDateOfBirth(updated.getDateOfBirth());
                existing.setGender(updated.getGender());
                existing.setPhone(updated.getPhone());
                existing.setEmail(updated.getEmail());
                existing.setBloodType(updated.getBloodType());
                existing.setAllergies(updated.getAllergies());
                existing.setNotes(updated.getNotes());
                persist();
                return;
            }
        }
    }

    public void deletePatient(String id) {
        patients.removeIf(p -> p.getId().equals(id));
        persist();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Patient getById(String id) {
        return patients.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst().orElse(null);
    }

    /**
     * Case-insensitive search across name, phone, email, blood type.
     */
    public List<Patient> search(String query) {
        if (query == null || query.isBlank()) return getAllPatients();
        String q = query.toLowerCase().trim();
        return patients.stream()
            .filter(p ->
                p.getFullName().toLowerCase().contains(q) ||
                p.getPhone().toLowerCase().contains(q)    ||
                p.getEmail().toLowerCase().contains(q)    ||
                p.getBloodType().toLowerCase().contains(q)
            )
            .collect(Collectors.toList());
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    public void persist() {
        try { DataStore.savePatients(patients); }
        catch (IOException e) { System.err.println("Failed to save patients: " + e.getMessage()); }
    }

    // ── Sample data ───────────────────────────────────────────────────────────

    private void seedSampleData() {
        patients.add(new Patient("Sarah", "Johnson", "1985-03-14", "Female",
            "614-555-0101", "sjohnson@email.com", "A+", "Penicillin", "Hypertension — monitor BP"));
        patients.add(new Patient("Michael", "Chen", "1972-11-28", "Male",
            "614-555-0182", "mchen@email.com", "O-", "None", "Type 2 diabetes, annual eye exam needed"));
        patients.add(new Patient("Emily", "Rodriguez", "1990-07-04", "Female",
            "614-555-0247", "erodriguez@email.com", "B+", "Sulfa drugs, shellfish", "Asthma — carry inhaler"));
        patients.add(new Patient("James", "Williams", "1965-01-19", "Male",
            "614-555-0318", "jwilliams@email.com", "AB+", "Aspirin", "Post-cardiac event, monthly check-ins"));
        patients.add(new Patient("Aisha", "Patel", "2000-09-30", "Female",
            "614-555-0409", "apatel@email.com", "O+", "Latex", "Seasonal allergies, otherwise healthy"));
        persist();
    }
}
