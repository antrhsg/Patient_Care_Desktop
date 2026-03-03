package view;

import controller.PatientController;
import model.Appointment;
import model.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Modal dialog for adding or editing an appointment.
 */
public class AppointmentFormDialog extends JDialog {

    private Appointment result = null;

    private final JComboBox<String> patientBox;
    private final List<Patient> patientList;
    private final JTextField dateField   = new JTextField("2025-06-01", 12);
    private final JTextField timeField   = new JTextField("09:00", 8);
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{
        "Check-up", "Follow-up", "Consultation", "Lab Results", "Procedure", "Emergency", "Other"
    });
    private final JTextField doctorField = new JTextField(20);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{
        "Scheduled", "Completed", "Cancelled", "No Show"
    });
    private final JTextArea notesArea = new JTextArea(3, 22);

    public AppointmentFormDialog(Frame owner, String title,
                                 PatientController patientController,
                                 Appointment existing) {
        super(owner, title, true);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new EmptyBorder(16, 20, 8, 20));

        patientList = patientController.getAllPatients();
        String[] names = patientList.stream()
            .map(p -> p.getFullName() + " (" + p.getDateOfBirth() + ")")
            .toArray(String[]::new);
        patientBox = new JComboBox<>(names);

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null) populate(existing);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        int row = 0;
        addRow(panel, lc, fc, row++, "Patient *",   patientBox);
        addRow(panel, lc, fc, row++, "Date *",      dateField);
        addRow(panel, lc, fc, row++, "Time *",      timeField);
        addRow(panel, lc, fc, row++, "Type",        typeBox);
        addRow(panel, lc, fc, row++, "Doctor",      doctorField);
        addRow(panel, lc, fc, row++, "Status",      statusBox);

        lc.gridy = row; lc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Notes"), lc);
        fc.gridy = row;
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(notesArea), fc);

        JLabel hint = new JLabel("  * Required.  Date: YYYY-MM-DD   Time: HH:MM");
        hint.setForeground(Color.GRAY);
        hint.setFont(hint.getFont().deriveFont(10f));
        GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0; hc.gridy = ++row; hc.gridwidth = 2;
        hc.insets = new Insets(8, 0, 0, 0);
        panel.add(hint, hc);

        return panel;
    }

    private void addRow(JPanel p, GridBagConstraints lc, GridBagConstraints fc,
                        int row, String label, JComponent field) {
        lc.gridy = row; fc.gridy = row;
        p.add(new JLabel(label), lc);
        p.add(field, fc);
    }

    private GridBagConstraints labelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.insets = new Insets(5, 0, 5, 10);
        c.anchor = GridBagConstraints.EAST;
        return c;
    }

    private GridBagConstraints fieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1; c.insets = new Insets(5, 0, 5, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        return c;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton okBtn     = new JButton("Save Appointment");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.setBackground(new Color(30, 100, 170));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);

        okBtn.addActionListener(e -> onSave());
        cancelBtn.addActionListener(e -> dispose());

        panel.add(cancelBtn);
        panel.add(okBtn);
        return panel;
    }

    private void onSave() {
        int idx = patientBox.getSelectedIndex();
        if (idx < 0 || patientList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please select a patient.", "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        if (date.isEmpty() || time.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Date and time are required.", "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        Patient selected = patientList.get(idx);
        result = new Appointment(
            selected.getId(),
            selected.getFullName(),
            date, time,
            (String) typeBox.getSelectedItem(),
            doctorField.getText().trim(),
            notesArea.getText().trim(),
            (String) statusBox.getSelectedItem()
        );
        dispose();
    }

    private void populate(Appointment a) {
        for (int i = 0; i < patientList.size(); i++) {
            if (patientList.get(i).getId().equals(a.getPatientId())) {
                patientBox.setSelectedIndex(i); break;
            }
        }
        dateField.setText(a.getDate());
        timeField.setText(a.getTime());
        typeBox.setSelectedItem(a.getType());
        doctorField.setText(a.getDoctor());
        statusBox.setSelectedItem(a.getStatus());
        notesArea.setText(a.getNotes());
    }

    public Appointment getResult() { return result; }
}
