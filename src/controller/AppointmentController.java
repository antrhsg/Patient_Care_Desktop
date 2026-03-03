package controller;

import model.Appointment;
import util.DataStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller layer for appointment operations.
 */
public class AppointmentController {

    private final List<Appointment> appointments = new ArrayList<>();

    public AppointmentController() {
        appointments.addAll(DataStore.loadAppointments());
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public void addAppointment(Appointment a) {
        appointments.add(a);
        persist();
    }

    public void updateAppointment(String id, Appointment updated) {
        for (Appointment a : appointments) {
            if (a.getId().equals(id)) {
                a.setPatientId(updated.getPatientId());
                a.setPatientName(updated.getPatientName());
                a.setDate(updated.getDate());
                a.setTime(updated.getTime());
                a.setType(updated.getType());
                a.setDoctor(updated.getDoctor());
                a.setNotes(updated.getNotes());
                a.setStatus(updated.getStatus());
                persist();
                return;
            }
        }
    }

    public void deleteAppointment(String id) {
        appointments.removeIf(a -> a.getId().equals(id));
        persist();
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public List<Appointment> getByPatient(String patientId) {
        return appointments.stream()
            .filter(a -> a.getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public List<Appointment> getByDate(String date) {
        return appointments.stream()
            .filter(a -> a.getDate().equals(date))
            .collect(Collectors.toList());
    }

    /**
     * Search appointments by patient name, doctor, type, date, or status.
     */
    public List<Appointment> search(String query) {
        if (query == null || query.isBlank()) return getAllAppointments();
        String q = query.toLowerCase().trim();
        return appointments.stream()
            .filter(a ->
                a.getPatientName().toLowerCase().contains(q) ||
                a.getDoctor().toLowerCase().contains(q)      ||
                a.getType().toLowerCase().contains(q)        ||
                a.getDate().contains(q)                      ||
                a.getStatus().toLowerCase().contains(q)
            )
            .collect(Collectors.toList());
    }

    /** Called when a patient is deleted — cleans up their appointments. */
    public void deleteByPatient(String patientId) {
        appointments.removeIf(a -> a.getPatientId().equals(patientId));
        persist();
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    public void persist() {
        try { DataStore.saveAppointments(appointments); }
        catch (IOException e) { System.err.println("Failed to save appointments: " + e.getMessage()); }
    }
}
