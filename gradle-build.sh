#!/bin/bash
set -e

echo "=== Cleaning project ==="
gradle clean

echo ""
echo "=== Listing Gradle projects ==="
gradle projects

echo ""
echo "=== Listing available tasks ==="
gradle tasks --all

echo ""
echo "=== Building APK ==="
gradle assembleRelease

echo ""
echo "=== Building AAB ==="
gradle bundleRelease

echo ""
echo "=== Build completed successfully ==="
