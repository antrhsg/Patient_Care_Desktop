package util;

import model.Appointment;
import model.Patient;

import java.io.*;
import java.util.List;

/**
 * Exports patient and appointment data to CSV format.
 */
public class CSVExporter {

    public static void exportPatients(List<Patient> patients, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,First Name,Last Name,Date of Birth,Gender,Phone,Email,Blood Type,Allergies,Notes");
            for (Patient p : patients) {
                pw.println(
                    csvField(p.getId()) + "," +
                    csvField(p.getFirstName()) + "," +
                    csvField(p.getLastName()) + "," +
                    csvField(p.getDateOfBirth()) + "," +
                    csvField(p.getGender()) + "," +
                    csvField(p.getPhone()) + "," +
                    csvField(p.getEmail()) + "," +
                    csvField(p.getBloodType()) + "," +
                    csvField(p.getAllergies()) + "," +
                    csvField(p.getNotes())
                );
            }
        }
    }

    public static void exportAppointments(List<Appointment> appointments, File file) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("ID,Patient ID,Patient Name,Date,Time,Type,Doctor,Notes,Status");
            for (Appointment a : appointments) {
                pw.println(
                    csvField(a.getId()) + "," +
                    csvField(a.getPatientId()) + "," +
                    csvField(a.getPatientName()) + "," +
                    csvField(a.getDate()) + "," +
                    csvField(a.getTime()) + "," +
                    csvField(a.getType()) + "," +
                    csvField(a.getDoctor()) + "," +
                    csvField(a.getNotes()) + "," +
                    csvField(a.getStatus())
                );
            }
        }
    }

    /** Wraps a value in quotes and escapes any internal quotes per RFC 4180. */
    private static String csvField(String value) {
        if (value == null) return "\"\"";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
