package view;

import controller.AppointmentController;
import controller.PatientController;
import model.Appointment;
import util.CSVExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * The Appointments tab — searchable JTable of all appointments,
 * color-coded by status, with Add / Edit / Delete / Export.
 */
public class AppointmentPanel extends JPanel {

    private final AppointmentController appointmentController;
    private final PatientController patientController;

    private AppointmentTableModel tableModel;
    private JTable table;

    public AppointmentPanel(AppointmentController ac, PatientController pc) {
        this.appointmentController = ac;
        this.patientController     = pc;
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTable(),   BorderLayout.CENTER);
    }

    // ── Toolbar ───────────────────────────────────────────────────────────────

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));

        JTextField searchField = new JTextField(22);
        searchField.putClientProperty("JTextField.placeholderText", "Search by patient, doctor, type, date, status…");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterTable(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { filterTable(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterTable(searchField.getText()); }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        searchPanel.add(new JLabel("🔍 Search:"));
        searchPanel.add(searchField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton addBtn    = styledButton("＋ Schedule",   new Color(34, 139, 34));
        JButton editBtn   = styledButton("✎ Edit",       new Color(30, 100, 170));
        JButton deleteBtn = styledButton("✕ Cancel Appt", new Color(180, 40, 40));
        JButton exportBtn = styledButton("⬇ Export CSV", new Color(100, 80, 160));

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

    // ── Table ─────────────────────────────────────────────────────────────────

    private JScrollPane buildTable() {
        tableModel = new AppointmentTableModel(appointmentController.getAllAppointments());
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        // Column widths
        int[] widths = {160, 80, 70, 110, 140, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Color-code rows by status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    String status = (String) tableModel.getValueAt(row, 5);
                    c.setBackground(switch (status) {
                        case "Completed"  -> new Color(220, 255, 220);
                        case "Cancelled"  -> new Color(255, 230, 230);
                        case "No Show"    -> new Color(255, 245, 200);
                        default           -> Color.WHITE;
                    });
                }
                return c;
            }
        });

        return new JScrollPane(table);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void onAdd() {
        if (patientController.getAllPatients().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Add at least one patient before scheduling an appointment.",
                "No Patients", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        AppointmentFormDialog dlg = new AppointmentFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Schedule Appointment", patientController, null);
        dlg.setVisible(true);
        Appointment a = dlg.getResult();
        if (a != null) {
            appointmentController.addAppointment(a);
            reloadTable();
        }
    }

    private void onEdit() {
        Appointment selected = getSelectedAppointment();
        if (selected == null) { warn("Please select an appointment to edit."); return; }
        AppointmentFormDialog dlg = new AppointmentFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Edit Appointment", patientController, selected);
        dlg.setVisible(true);
        Appointment updated = dlg.getResult();
        if (updated != null) {
            appointmentController.updateAppointment(selected.getId(), updated);
            reloadTable();
        }
    }

    private void onDelete() {
        Appointment selected = getSelectedAppointment();
        if (selected == null) { warn("Please select an appointment to remove."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove appointment for " + selected.getPatientName() + " on " + selected.getDate() + "?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentController.deleteAppointment(selected.getId());
            reloadTable();
        }
    }

    private void onExport() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("appointments_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CSVExporter.exportAppointments(
                    appointmentController.getAllAppointments(), fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Exported successfully!", "Export",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filterTable(String query) {
        tableModel.setData(appointmentController.search(query));
    }

    private void reloadTable() {
        tableModel.setData(appointmentController.getAllAppointments());
    }

    private Appointment getSelectedAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        return tableModel.getAppointment(row);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "No Selection", JOptionPane.INFORMATION_MESSAGE);
    }

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

    static class AppointmentTableModel extends AbstractTableModel {
        private static final String[] COLS = {"Patient", "Date", "Time", "Type", "Doctor", "Status"};
        private List<Appointment> data;

        AppointmentTableModel(List<Appointment> data) { this.data = data; }

        public void setData(List<Appointment> d) { this.data = d; fireTableDataChanged(); }
        public Appointment getAppointment(int row) { return data.get(row); }

        @Override public int getRowCount()    { return data.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Appointment a = data.get(row);
            return switch (col) {
                case 0 -> a.getPatientName();
                case 1 -> a.getDate();
                case 2 -> a.getTime();
                case 3 -> a.getType();
                case 4 -> a.getDoctor();
                case 5 -> a.getStatus();
                default -> "";
            };
        }
    }
}
