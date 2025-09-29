#!/bin/bash

# Make gradlew executable
chmod +x ./gradlew

# List available projects
echo "=== Listing Gradle projects ==="
./gradlew projects

echo ""
echo "=== Listing available tasks ==="
./gradlew tasks

echo ""
echo "=== Building APK ==="
./gradlew assembleRelease

echo ""
echo "=== Building AAB ==="
./gradlew bundleRelease
