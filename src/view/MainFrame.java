package view;

import controller.AppointmentController;
import controller.PatientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Top-level application window.
 * Owns the two controllers and houses the tabbed pane.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        super("PatientCare Desktop");

        // Controllers — single source of truth for all data
        PatientController patientController         = new PatientController();
        AppointmentController appointmentController = new AppointmentController();

        // Build tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("👤  Patients",     new PatientPanel(patientController, appointmentController));
        tabs.addTab("📅  Appointments", new AppointmentPanel(appointmentController, patientController));

        // Status bar
        JLabel statusBar = new JLabel("  PatientCare v1.0  |  Data auto-saved to data/patients.json");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setFont(statusBar.getFont().deriveFont(11f));
        statusBar.setForeground(Color.GRAY);

        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Persist on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                patientController.persist();
                appointmentController.persist();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(960, 640));
        pack();
        setLocationRelativeTo(null);  // center on screen
        setVisible(true);
    }
}
