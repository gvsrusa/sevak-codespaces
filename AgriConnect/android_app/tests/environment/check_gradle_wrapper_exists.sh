#!/bin/bash

# Script to check for the absence of Gradle wrapper files
# Exits with 1 (failure) if all specified Gradle wrapper components are MISSING.
# Exits with 0 (success) if ANY of the components are found.

GRADLEW_PATH="./gradlew"
GRADLEW_BAT_PATH="./gradlew.bat"
GRADLE_WRAPPER_PROPERTIES_PATH="./gradle/wrapper/gradle-wrapper.properties"

# Check if any of the files exist
if [ -f "$GRADLEW_PATH" ] || [ -f "$GRADLEW_BAT_PATH" ] || [ -f "$GRADLE_WRAPPER_PROPERTIES_PATH" ]; then
  echo "At least one Gradle wrapper component found. Test 'passes' (bug not reproduced)."
  exit 0 # Indicates bug is NOT reproduced (files exist)
else
  echo "All specified Gradle wrapper components are missing. Test 'fails' (bug reproduced)."
  exit 1 # Indicates bug IS reproduced (files are missing)
fi