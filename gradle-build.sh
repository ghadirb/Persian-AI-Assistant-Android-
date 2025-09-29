#!/bin/bash
set -e

echo "=== Starting Persian AI Assistant Build ==="
echo "Using minimal configuration for compatibility"

echo ""
echo "=== Cleaning project ==="
./gradlew clean --no-daemon --stacktrace

echo ""
echo "=== Building Debug APK (Minimal Version) ==="
./gradlew assembleDebug --no-daemon --stacktrace --info

echo ""
echo "=== Checking build outputs ==="
find app/build/outputs -name "*.apk" -type f || echo "No APK files found"

echo ""
echo "=== Build completed successfully ==="
