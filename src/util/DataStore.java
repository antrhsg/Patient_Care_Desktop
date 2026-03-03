package util;

import model.Appointment;
import model.Patient;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing application data as JSON.
 * Hand-rolled JSON to avoid external dependencies — covers exactly
 * the data shapes used by Patient and Appointment.
 */
public class DataStore {

    private static final String DATA_DIR  = "data";
    private static final String PAT_FILE  = DATA_DIR + "/patients.json";
    private static final String APT_FILE  = DATA_DIR + "/appointments.json";

    // ── Save ─────────────────────────────────────────────────────────────────

    public static void savePatients(List<Patient> patients) throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < patients.size(); i++) {
            sb.append(toJson(patients.get(i)));
            if (i < patients.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        Files.writeString(Paths.get(PAT_FILE), sb.toString());
    }

    public static void saveAppointments(List<Appointment> appointments) throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < appointments.size(); i++) {
            sb.append(toJson(appointments.get(i)));
            if (i < appointments.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        Files.writeString(Paths.get(APT_FILE), sb.toString());
    }

    // ── Load ─────────────────────────────────────────────────────────────────

    public static List<Patient> loadPatients() {
        List<Patient> list = new ArrayList<>();
        if (!Files.exists(Paths.get(PAT_FILE))) return list;
        try {
            String content = Files.readString(Paths.get(PAT_FILE));
            List<String> blocks = splitObjects(content);
            for (String block : blocks) {
                list.add(patientFromJson(block));
            }
        } catch (Exception e) {
            System.err.println("Warning: could not load patients.json — " + e.getMessage());
        }
        return list;
    }

    public static List<Appointment> loadAppointments() {
        List<Appointment> list = new ArrayList<>();
        if (!Files.exists(Paths.get(APT_FILE))) return list;
        try {
            String content = Files.readString(Paths.get(APT_FILE));
            List<String> blocks = splitObjects(content);
            for (String block : blocks) {
                list.add(appointmentFromJson(block));
            }
        } catch (Exception e) {
            System.err.println("Warning: could not load appointments.json — " + e.getMessage());
        }
        return list;
    }

    // ── Serialization helpers ─────────────────────────────────────────────────

    private static String toJson(Patient p) {
        return "  {" +
            field("id",          p.getId())          +
            field("firstName",   p.getFirstName())   +
            field("lastName",    p.getLastName())     +
            field("dateOfBirth", p.getDateOfBirth())  +
            field("gender",      p.getGender())       +
            field("phone",       p.getPhone())        +
            field("email",       p.getEmail())        +
            field("bloodType",   p.getBloodType())    +
            field("allergies",   p.getAllergies())     +
            lastField("notes",   p.getNotes())        +
            "  }";
    }

    private static String toJson(Appointment a) {
        return "  {" +
            field("id",          a.getId())          +
            field("patientId",   a.getPatientId())   +
            field("patientName", a.getPatientName()) +
            field("date",        a.getDate())         +
            field("time",        a.getTime())         +
            field("type",        a.getType())         +
            field("doctor",      a.getDoctor())       +
            field("notes",       a.getNotes())        +
            lastField("status",  a.getStatus())       +
            "  }";
    }

    private static String field(String key, String value) {
        return "\"" + key + "\":\"" + escape(value) + "\",";
    }

    private static String lastField(String key, String value) {
        return "\"" + key + "\":\"" + escape(value) + "\"";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "");
    }

    // ── Deserialization helpers ───────────────────────────────────────────────

    private static List<String> splitObjects(String json) {
        List<String> blocks = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') { if (depth++ == 0) start = i; }
            else if (c == '}') { if (--depth == 0 && start >= 0) blocks.add(json.substring(start, i + 1)); }
        }
        return blocks;
    }

    private static String readField(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return "";
        start += search.length();
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) { sb.append(c == 'n' ? '\n' : c); escaped = false; }
            else if (c == '\\') escaped = true;
            else if (c == '"') break;
            else sb.append(c);
        }
        return sb.toString();
    }

    private static Patient patientFromJson(String json) {
        return new Patient(
            readField(json, "id"),
            readField(json, "firstName"),
            readField(json, "lastName"),
            readField(json, "dateOfBirth"),
            readField(json, "gender"),
            readField(json, "phone"),
            readField(json, "email"),
            readField(json, "bloodType"),
            readField(json, "allergies"),
            readField(json, "notes")
        );
    }

    private static Appointment appointmentFromJson(String json) {
        return new Appointment(
            readField(json, "id"),
            readField(json, "patientId"),
            readField(json, "patientName"),
            readField(json, "date"),
            readField(json, "time"),
            readField(json, "type"),
            readField(json, "doctor"),
            readField(json, "notes"),
            readField(json, "status")
        );
    }
}
