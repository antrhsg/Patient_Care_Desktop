package view;

import controller.AppointmentController;
import controller.PatientController;
import model.Patient;
import util.CSVExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * The Patients tab — JTable of all patients, live search,
 * detail panel, and Add / Edit / Delete / Export actions.
 */
public class PatientPanel extends JPanel {

    private final PatientController patientController;
    private final AppointmentController appointmentController;

    // Table
    private PatientTableModel tableModel;
    private JTable table;
    private TableRowSorter<PatientTableModel> sorter;

    // Detail panel fields
    private JLabel detailName     = new JLabel("—");
    private JLabel detailDob      = new JLabel("—");
    private JLabel detailGender   = new JLabel("—");
    private JLabel detailPhone    = new JLabel("—");
    private JLabel detailEmail    = new JLabel("—");
    private JLabel detailBlood    = new JLabel("—");
    private JLabel detailAllergy  = new JLabel("—");
    private JTextArea detailNotes = new JTextArea(4, 20);

    public PatientPanel(PatientController pc, AppointmentController ac) {
        this.patientController      = pc;
        this.appointmentController  = ac;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildSplitPane(), BorderLayout.CENTER);
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));

        // Search
        JTextField searchField = new JTextField(22);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name, phone, email, blood type…");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterTable(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { filterTable(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        searchPanel.add(new JLabel("🔍 Search:"));
        searchPanel.add(searchField);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton addBtn    = styledButton("＋ Add Patient",  new Color(34, 139, 34));
        JButton editBtn   = styledButton("✎ Edit",         new Color(30, 100, 170));
        JButton deleteBtn = styledButton("✕ Delete",       new Color(180, 40, 40));
        JButton exportBtn = styledButton("⬇ Export CSV",   new Color(100, 80, 160));

        addBtn.addActionListener(e    -> onAdd());
        editBtn.addActionListener(e   -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());
        exportBtn.addActionListener(e -> onExport());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(Box.createHorizontalStrut(8));
        btnPanel.add(exportBtn);

        bar.add(searchPanel, BorderLayout.WEST);
        bar.add(btnPanel,    BorderLayout.EAST);
        return bar;
    }

    // ── Split pane ────────────────────────────────────────────────────────────

    private JSplitPane buildSplitPane() {
        tableModel = new PatientTableModel(patientController.getAllPatients());
        table      = new JTable(tableModel);
        sorter     = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshDetail();
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(table), buildDetailPanel());
        split.setDividerLocation(560);
        split.setResizeWeight(0.6);
        return split;
    }

    // ── Detail panel ──────────────────────────────────────────────────────────

    private JPanel buildDetailPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Patient Details"),
            new EmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.anchor = GridBagConstraints.EAST;
        lc.insets = new Insets(4, 0, 4, 8);

        GridBagConstraints vc = new GridBagConstraints();
        vc.gridx = 1; vc.anchor = GridBagConstraints.WEST;
        vc.fill = GridBagConstraints.HORIZONTAL; vc.weightx = 1.0;
        vc.insets = new Insets(4, 0, 4, 0);

        detailName.setFont(detailName.getFont().deriveFont(Font.BOLD, 14f));

        String[][] rows = {
            {"Name:",      null},
            {"DOB:",       null},
            {"Gender:",    null},
            {"Phone:",     null},
            {"Email:",     null},
            {"Blood:",     null},
            {"Allergies:", null}
        };
        JLabel[] valueLabels = {detailName, detailDob, detailGender,
                                detailPhone, detailEmail, detailBlood, detailAllergy};

        for (int i = 0; i < rows.length; i++) {
            lc.gridy = i; vc.gridy = i;
            JLabel l = new JLabel(rows[i][0]);
            l.setForeground(Color.GRAY);
            panel.add(l, lc);
            panel.add(valueLabels[i], vc);
        }

        // Notes
        int nr = rows.length;
        lc.gridy = nr; lc.anchor = GridBagConstraints.NORTHEAST;
        JLabel nl = new JLabel("Notes:");
        nl.setForeground(Color.GRAY);
        panel.add(nl, lc);
        vc.gridy = nr; vc.fill = GridBagConstraints.BOTH; vc.weighty = 1.0;
        detailNotes.setEditable(false);
        detailNotes.setLineWrap(true);
        detailNotes.setWrapStyleWord(true);
        detailNotes.setBackground(panel.getBackground());
        panel.add(new JScrollPane(detailNotes), vc);

        return panel;
    }

    private void refreshDetail() {
        int row = table.getSelectedRow();
        if (row < 0) { clearDetail(); return; }
        Patient p = tableModel.getPatient(table.convertRowIndexToModel(row));
        detailName.setText(p.getFullName());
        detailDob.setText(p.getDateOfBirth());
        detailGender.setText(p.getGender());
        detailPhone.setText(p.getPhone());
        detailEmail.setText(p.getEmail());
        detailBlood.setText(p.getBloodType());
        detailAllergy.setText(p.getAllergies().isEmpty() ? "None" : p.getAllergies());
        detailNotes.setText(p.getNotes());
    }

    private void clearDetail() {
        detailName.setText("—"); detailDob.setText("—"); detailGender.setText("—");
        detailPhone.setText("—"); detailEmail.setText("—");
        detailBlood.setText("—"); detailAllergy.setText("—");
        detailNotes.setText("");
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void onAdd() {
        PatientFormDialog dlg = new PatientFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), "Add New Patient", null);
        dlg.setVisible(true);
        Patient p = dlg.getResult();
        if (p != null) {
            patientController.addPatient(p);
            reloadTable();
        }
    }

    private void onEdit() {
        Patient selected = getSelectedPatient();
        if (selected == null) { warn("Please select a patient to edit."); return; }
        PatientFormDialog dlg = new PatientFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), "Edit Patient", selected);
        dlg.setVisible(true);
        Patient updated = dlg.getResult();
        if (updated != null) {
            patientController.updatePatient(selected.getId(), updated);
            reloadTable();
        }
    }

    private void onDelete() {
        Patient selected = getSelectedPatient();
        if (selected == null) { warn("Please select a patient to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete " + selected.getFullName() + " and all their appointments?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentController.deleteByPatient(selected.getId());
            patientController.deletePatient(selected.getId());
            reloadTable();
            clearDetail();
        }
    }

    private void onExport() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("patients_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVExporter.exportPatients(patientController.getAllPatients(), fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterTable(String query) {
        List<Patient> filtered = patientController.search(query);
        tableModel.setData(filtered);
    }

    private void reloadTable() {
        tableModel.setData(patientController.getAllPatients());
        refreshDetail();
    }

    private Patient getSelectedPatient() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return tableModel.getPatient(table.convertRowIndexToModel(row));
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }

    // ── Inner table model ─────────────────────────────────────────────────────

    static class PatientTableModel extends AbstractTableModel {
        private static final String[] COLS = {"Name", "Date of Birth", "Gender", "Phone", "Blood Type"};
        private List<Patient> data;

        PatientTableModel(List<Patient> data) { this.data = data; }

        public void setData(List<Patient> data) {
            this.data = data;
            fireTableDataChanged();
        }

        public Patient getPatient(int modelRow) { return data.get(modelRow); }

        @Override public int getRowCount()    { return data.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Patient p = data.get(row);
            return switch (col) {
                case 0 -> p.getFullName();
                case 1 -> p.getDateOfBirth();
                case 2 -> p.getGender();
                case 3 -> p.getPhone();
                case 4 -> p.getBloodType();
                default -> "";
            };
        }
    }
}
