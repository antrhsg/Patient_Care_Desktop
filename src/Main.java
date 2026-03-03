import view.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 * Launches the Swing UI on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        // Use system look and feel for a native feel on any OS
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // All Swing work must happen on the EDT
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
