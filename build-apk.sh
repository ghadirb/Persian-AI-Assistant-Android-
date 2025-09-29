#!/bin/bash

echo "=== Persian AI Assistant APK Build ==="

# Check if we're in the right directory
echo "Current directory: $(pwd)"
echo "Directory contents:"
ls -la

# Setup Android SDK
echo "=== Setting up Android SDK ==="
echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
echo "Android SDK Root: $ANDROID_SDK_ROOT"

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo "❌ gradlew not found in current directory!"
    echo "Looking for gradlew..."
    find . -name "gradlew" -type f
    exit 1
fi

# Make gradlew executable
echo "=== Making gradlew executable ==="
chmod +x ./gradlew

# Check Gradle version
echo "=== Checking Gradle version ==="
./gradlew --version

# Clean and build
echo "=== Cleaning project ==="
./gradlew clean --no-daemon --stacktrace

echo "=== Building Debug APK ==="
./gradlew assembleDebug --no-daemon --stacktrace --info

echo "=== Building Release APK ==="
./gradlew assembleRelease --no-daemon --stacktrace --info

echo "=== Building AAB (Android App Bundle) ==="
./gradlew bundleRelease --no-daemon --stacktrace --info

echo "=== Checking build outputs ==="
echo "Build directory contents:"
ls -la app/build/ 2>/dev/null || echo "No build directory found"

echo "=== Finding APK files ==="
find . -name "*.apk" -type f 2>/dev/null || echo "No APK files found"

echo "=== Creating artifacts directory ==="
mkdir -p artifacts

echo "=== Copying APK files to artifacts directory ==="
APK_FILES=$(find . -name "*.apk" -type f 2>/dev/null)
if [ -n "$APK_FILES" ]; then
    echo "Found APK files:"
    echo "$APK_FILES"

    # Copy all APK files to artifacts directory
    find . -name "*.apk" -type f -exec cp {} artifacts/ \;

    # Also copy to root for backup
    find . -name "*.apk" -type f -exec cp {} . \;

    echo "APK files copied to artifacts directory:"
    ls -la artifacts/

    echo "APK files in root:"
    ls -la *.apk 2>/dev/null || echo "No APK files in root"
else
    echo "❌ No APK files found anywhere!"
    echo "Checking gradle tasks..."
    ./gradlew tasks --all | grep -i apk || echo "No APK tasks found"
    exit 1
fi

echo "=== Checking AAB files ==="
AAB_FILES=$(find . -name "*.aab" -type f 2>/dev/null)
if [ -n "$AAB_FILES" ]; then
    echo "Found AAB files:"
    echo "$AAB_FILES"
    find . -name "*.aab" -type f -exec cp {} artifacts/ \;
    echo "AAB files copied to artifacts:"
    ls -la artifacts/*.aab 2>/dev/null || echo "No AAB files"
fi

echo "=== Build completed successfully ==="
