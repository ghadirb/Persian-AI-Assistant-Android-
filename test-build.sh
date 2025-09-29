#!/bin/bash

echo "=== Testing Persian AI App Build ==="
echo "Step 1: Cleaning project..."
./gradlew clean

echo "Step 2: Building debug APK..."
./gradlew assembleDebug --stacktrace --info

echo "Step 3: Checking output..."
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "✅ Build successful! APK created:"
    ls -la app/build/outputs/apk/debug/
else
    echo "❌ Build failed! No APK found."
    exit 1
fi
