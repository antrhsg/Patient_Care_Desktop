package view;

import model.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modal dialog for adding or editing a patient record.
 * Returns the filled Patient object via getResult() after OK is pressed.
 */
public class PatientFormDialog extends JDialog {

    private Patient result = null;

    // Fields
    private final JTextField firstNameField   = new JTextField(18);
    private final JTextField lastNameField    = new JTextField(18);
    private final JTextField dobField         = new JTextField(12);  // YYYY-MM-DD
    private final JComboBox<String> genderBox = new JComboBox<>(new String[]{"Female", "Male", "Non-binary", "Prefer not to say"});
    private final JTextField phoneField       = new JTextField(15);
    private final JTextField emailField       = new JTextField(22);
    private final JComboBox<String> bloodBox  = new JComboBox<>(new String[]{"A+","A-","B+","B-","AB+","AB-","O+","O-","Unknown"});
    private final JTextField allergiesField   = new JTextField(22);
    private final JTextArea notesArea         = new JTextArea(3, 22);

    public PatientFormDialog(Frame owner, String title, Patient existing) {
        super(owner, title, true);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new EmptyBorder(16, 20, 8, 20));

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        if (existing != null) populate(existing);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    // ── Form layout ───────────────────────────────────────────────────────────

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        int row = 0;
        addRow(panel, lc, fc, row++, "First Name *",  firstNameField);
        addRow(panel, lc, fc, row++, "Last Name *",   lastNameField);
        addRow(panel, lc, fc, row++, "Date of Birth", dobField);
        addRow(panel, lc, fc, row++, "Gender",        genderBox);
        addRow(panel, lc, fc, row++, "Phone",         phoneField);
        addRow(panel, lc, fc, row++, "Email",         emailField);
        addRow(panel, lc, fc, row++, "Blood Type",    bloodBox);
        addRow(panel, lc, fc, row++, "Allergies",     allergiesField);

        // Notes label
        lc.gridy = row;
        lc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Notes"), lc);

        // Notes text area
        fc.gridy = row;
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(notesArea), fc);

        JLabel hint = new JLabel("  * Required fields.  DOB format: YYYY-MM-DD");
        hint.setForeground(Color.GRAY);
        hint.setFont(hint.getFont().deriveFont(10f));
        GridBagConstraints hc = new GridBagConstraints();
        hc.gridx = 0; hc.gridy = ++row; hc.gridwidth = 2;
        hc.insets = new Insets(8, 0, 0, 0);
        panel.add(hint, hc);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints lc, GridBagConstraints fc,
                        int row, String label, JComponent field) {
        lc.gridy = row; fc.gridy = row;
        lc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), lc);
        panel.add(field, fc);
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

    // ── Buttons ───────────────────────────────────────────────────────────────

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton okBtn     = new JButton("Save Patient");
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
        String first = firstNameField.getText().trim();
        String last  = lastNameField.getText().trim();
        if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "First and last name are required.", "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        result = new Patient(
            first, last,
            dobField.getText().trim(),
            (String) genderBox.getSelectedItem(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            (String) bloodBox.getSelectedItem(),
            allergiesField.getText().trim(),
            notesArea.getText().trim()
        );
        dispose();
    }

    private void populate(Patient p) {
        firstNameField.setText(p.getFirstName());
        lastNameField.setText(p.getLastName());
        dobField.setText(p.getDateOfBirth());
        genderBox.setSelectedItem(p.getGender());
        phoneField.setText(p.getPhone());
        emailField.setText(p.getEmail());
        bloodBox.setSelectedItem(p.getBloodType());
        allergiesField.setText(p.getAllergies());
        notesArea.setText(p.getNotes());
    }

    /** Returns the Patient built from the form, or null if cancelled. */
    public Patient getResult() {
        return result;
    }
}
