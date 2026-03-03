#!/bin/bash
# PatientCare build & run script
# Run from the PatientCare/ directory: ./run.sh

set -e

SRC_DIR="src"
OUT_DIR="out"
MAIN_CLASS="Main"

echo "🔨 Compiling PatientCare..."
mkdir -p "$OUT_DIR"

# Compile all .java files, putting .class files in out/
javac -d "$OUT_DIR" -sourcepath "$SRC_DIR" $(find "$SRC_DIR" -name "*.java")

echo "✅ Build successful."
echo "🚀 Launching PatientCare Desktop..."
echo ""

# Run from the project root so data/ folder is created in the right place
java -cp "$OUT_DIR" "$MAIN_CLASS"
