# PatientCare Desktop

A Java Swing desktop application for managing patient records and appointments.  
Built with clean MVC architecture.

## Features

- **Patient Records tab** — add, edit, delete, and search patients with live filtering  
- **Appointments tab** — schedule appointments per patient, color-coded by status  
- **Detail panel** — click any patient to see full profile instantly  
- **Persistent storage** — data auto-saves to `data/patients.json` and `data/appointments.json`  
- **CSV export** — export patients or appointments with one click  

## Requirements

- Java 14 or later 

## Running the app

```bash
# From the PatientCare/ directory:
chmod +x run.sh
./run.sh
```

Or manually:
```bash
mkdir -p out
javac -d out -sourcepath src $(find src -name "*.java")
java -cp out Main
```

## Project structure

```
PatientCare/
├── src/
│   ├── Main.java                        ← Entry point (EDT launch)
│   ├── model/
│   │   ├── Patient.java                 ← Patient data class
│   │   └── Appointment.java             ← Appointment data class
│   ├── view/
│   │   ├── MainFrame.java               ← Top-level JFrame + JTabbedPane
│   │   ├── PatientPanel.java            ← Patients tab (JTable + detail panel)
│   │   ├── AppointmentPanel.java        ← Appointments tab (JTable + status colors)
│   │   ├── PatientFormDialog.java       ← Add/edit patient modal dialog
│   │   └── AppointmentFormDialog.java   ← Add/edit appointment modal dialog
│   ├── controller/
│   │   ├── PatientController.java       ← Patient CRUD + search logic
│   │   └── AppointmentController.java   ← Appointment CRUD + search logic
│   └── util/
│       ├── DataStore.java               ← JSON read/write (no external libs)
│       └── CSVExporter.java             ← CSV export (RFC 4180 compliant)
├── data/                                ← Auto-created on first run
│   ├── patients.json
│   └── appointments.json
└── run.sh
```

## Architecture

**Model-View-Controller**

